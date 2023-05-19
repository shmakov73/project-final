package com.javarush.jira.profile.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.Profile;
import com.javarush.jira.profile.internal.ProfileMapper;
import com.javarush.jira.profile.internal.ProfileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.javarush.jira.common.util.JsonUtil.writeValue;
import static com.javarush.jira.profile.web.ProfileRestController.REST_URL;

import static com.javarush.jira.profile.web.ProfileTestData.ADMIN_MAIL;
import static com.javarush.jira.profile.web.ProfileTestData.ADMIN_PROFILE_TO;
import static com.javarush.jira.profile.web.ProfileTestData.PROFILE_TO_MATCHER;
import static com.javarush.jira.profile.web.ProfileTestData.UPDATED_USER_CONTACTS;
import static com.javarush.jira.profile.web.ProfileTestData.UPDATED_USER_MAIL_NOTIFICATIONS;
import static com.javarush.jira.profile.web.ProfileTestData.USER_MAIL;
import static com.javarush.jira.profile.web.ProfileTestData.USER_PASSWORD;
import static com.javarush.jira.profile.web.ProfileTestData.USER_PROFILE_TO;
import static com.javarush.jira.profile.web.ProfileTestData.getInvalidProfileToUpdate;
import static com.javarush.jira.profile.web.ProfileTestData.jsonWithPassword;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileRestControllerTest extends AbstractControllerTest {

    @Autowired
    private ProfileRepository repository;

    @Autowired
    ProfileMapper mapper;

    @Test
    void testGet_returnUnauthorized_whenUserNotAuth() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(REST_URL);


        perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testGet_returnUserProfileJSON_whenAuthUser() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(REST_URL);


        perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PROFILE_TO_MATCHER.contentJson(USER_PROFILE_TO));
    }


    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void testGet_returnAdminProfileJSON_whenAuthAdmin() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(REST_URL);


        perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PROFILE_TO_MATCHER.contentJson(ADMIN_PROFILE_TO));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testUpdate_returnUnprocessableEntity_whensProfileInvalid() throws Exception {

        ProfileTo invalidProfile = getInvalidProfileToUpdate();


        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalidProfile)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testUpdate_updateUserProfile_whenNewContactsProvided() throws Exception {
        // Arrange
        Profile dbProfileBefore = repository.getExisted(USER_PROFILE_TO.id());
        ProfileTo updatedTo = mapper.toTo(dbProfileBefore);


        updatedTo.setContacts(UPDATED_USER_CONTACTS);
        ResultActions resultActions = perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updatedTo, USER_PASSWORD)))
                .andDo(print());


        Profile dbProfileAfter = repository.getExisted(USER_PROFILE_TO.id());
        ProfileTo dbProfileToAfter = mapper.toTo(dbProfileAfter);
        resultActions.andExpect(status().isNoContent());
        Assertions.assertEquals(dbProfileToAfter.getContacts(), UPDATED_USER_CONTACTS);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testUpdate_updateUserProfile_whenNewMailNotificationsProvided() throws Exception {

        Profile dbProfileBefore = repository.getExisted(USER_PROFILE_TO.id());
        ProfileTo updatedTo = mapper.toTo(dbProfileBefore);


        updatedTo.setMailNotifications(UPDATED_USER_MAIL_NOTIFICATIONS);
        ResultActions resultActions = perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updatedTo, USER_PASSWORD)));


        resultActions.andExpect(status().isNoContent());
        Profile dbProfileAfter = repository.getExisted(USER_PROFILE_TO.id());
        ProfileTo dbProfileToAfter = mapper.toTo(dbProfileAfter);
        Assertions.assertEquals(dbProfileToAfter.getMailNotifications(), UPDATED_USER_MAIL_NOTIFICATIONS);
    }
}