## [REST API](http://localhost:8080/doc)

## Концепция:
- Spring Modulith
  - [Spring Modulith: достигли ли мы зрелости модульности](https://habr.com/ru/post/701984/)
  - [Introducing Spring Modulith](https://spring.io/blog/2022/10/21/introducing-spring-modulith)
  - [Spring Modulith - Reference documentation](https://docs.spring.io/spring-modulith/docs/current-SNAPSHOT/reference/html/)

```
  url: jdbc:postgresql://localhost:5432/jira
  username: jira
  password: JiraRush
```
- Есть 2 общие таблицы, на которых не fk
  - _Reference_ - справочник. Связь делаем по _code_ (по id нельзя, тк id привязано к окружению-конкретной базе)
  - _UserBelong_ - привязка юзеров с типом (owner, lead, ...) к объекту (таска, проект, спринт, ...). FK вручную будем проверять

## Аналоги
- https://java-source.net/open-source/issue-trackers

## Тестирование
- https://habr.com/ru/articles/259055/

Список выполненных задач:
1. Understand the project structure (onboarding).
2. Delete social networks: vk, yandex.
3. Transfer sensitive information (login, database password, identifiers for OAuth registration/authorization, mail settings) to a separate property file.
4. Redo the tests so that during the tests the in memory database (H2) is used, and not PostgreSQL.
5. Write tests for all public methods of the ProfileRestController controller.
6. Add new functionality: adding tags to the task.
7. Add the ability to subscribe to tasks that are not assigned to the current user.
8. Write a Dockerfile for the main server.
9. Write a docker-compose file to run the server container along with the database and nginx. For nginx, use the config/nginx.conf config file.
...
