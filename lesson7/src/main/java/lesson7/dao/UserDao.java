package lesson7.dao;

import lesson7.config.JpaConfig;
import lesson7.domain.Message;
import lesson7.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Слой DAO для пользователя
 */
public class UserDao {


    /**
     * Создание или обновление пользователя
     * @param user пользователь для создания
     * @return созданный пользователь
     */
    public User save(User user) {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();
        try {
            entityManager.getTransaction().begin();

            if (user.getUser_id() == null) {
                entityManager.persist(user);
            } else {
                user = entityManager.merge(user);
            }

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        }finally {
            entityManager.close();
        }

        return user;
    }

    /**
     * Удаление пользователя
     * @param user пользователь для удаления
     * @return успешно ли выполнено удаление
     */
    public boolean delete(User user) {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();
        boolean result = false;
        try {
            entityManager.getTransaction().begin();

            if (user.getUser_id() != null) {
                entityManager.remove(user);
                result = true;
            } else {
                System.out.println("Can't find and delete this user");
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        }finally {
            entityManager.close();
        }

        return result;
    }

    /**
     * Получение всех пользователей
     * @return список пользователей
     */
    public List<User> getAll(){
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);

        Root<User> root = query.from(User.class);
        query.select(root);

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Получение пользователя по ФИО
     * @param user пользователь
     * @return найденный пользователь
     */
    public User findByFullName(User user) {

        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);

        Root<User> root = query.from(User.class); // FROM AUTHOR a
        query.select(root)
                .where(criteriaBuilder.equal(root.get("firstName"), user.getFirstName()))
                .where(criteriaBuilder.equal(root.get("lastName"), user.getLastName()))
                .where(criteriaBuilder.equal(root.get("middleName"), user.getMiddleName()));

        return entityManager.createQuery(query).getSingleResult();
    }


    /**
     * Получение всех сообщений для данного пользователя
     * @param user пользователь
     * @return список сообщений
     */
    public List<Message> getAllMessages(User user) {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Message> query = criteriaBuilder.createQuery(Message.class);

        Root<Message> root = query.from(Message.class);
        query.select(root)
                .groupBy(root)
                .where(
                        criteriaBuilder.or(criteriaBuilder.equal(root.get("sender"), user)
                        , criteriaBuilder.equal(root.get("receiver"), user)));

        return entityManager.createQuery(query).getResultList();
    }



    public List<User> getAllDialogs(User user) {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);

        Root<Message> root = query.from(Message.class);
        query.select(root.get("receiver"))
                .where(
                        criteriaBuilder.equal(root.get("sender"), user)
                );
        List<User> users = entityManager.createQuery(query).getResultList();
        query.select(root.get("sender"))
                .where(
                        criteriaBuilder.equal(root.get("receiver"), user)
                );
        users.addAll(entityManager.createQuery(query).getResultList());

        return users.stream().distinct().collect(Collectors.toList());
    }


    /**
     * Получение всех сообщений между данными пользователями
     * @param user1 первый пользователь
     * @param user2 второй пользователь
     * @return @return список сообщений
     */
    public List<Message> getDialogMessages(User user1, User user2) {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Message> query = criteriaBuilder.createQuery(Message.class);

        Root<Message> root = query.from(Message.class);
        query.select(root)
                .groupBy(root)
                .where(
                        criteriaBuilder.or(
                                usersAreInterlocutors(criteriaBuilder, root, user1, user2)
                                , usersAreInterlocutors(criteriaBuilder, root, user2, user1)
                        )
                );
        List<Message> messages = entityManager.createQuery(query).getResultList();
        return messages;
    }

    /**
     * Проверка являются ли пользователи собеседниками
     * @param criteriaBuilder создатель критериев запросов
     * @param root корневой тип запросов
     * @param user1 первый пользователь
     * @param user2 второй пользователь
     * @return Предикат о том, являются ли пользователи собеседниками
     */
    private Predicate usersAreInterlocutors(CriteriaBuilder criteriaBuilder, Root<Message> root, User user1, User user2){
        return criteriaBuilder.and(criteriaBuilder.equal(root.get("sender"), user1),
                criteriaBuilder.equal(root.get("receiver"), user2));
    }
}
