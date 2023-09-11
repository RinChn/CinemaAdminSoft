package sp.back;

import org.apache.log4j.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Синглтон для использования единого во всем проекте EntityManagerFactory, 
 * а также общей работе с EntityManager.
 * @author Арина Морик
 *
 */
public class Singleton {     
	/** Экземпляр класса для всего проекта**/
	private static Singleton instance = null;     
	/** Фабрика EntityManager для работы с БД cinema **/
	private EntityManagerFactory emf;   
	/** Логгер класса **/
	private static final Logger log = Logger.getLogger(Singleton.class.getName());
	
	/** 
	 * Создание фабрики
	 */
	private Singleton() {     
		log.info("Создание фабрики EntityManager");
		emf = Persistence.createEntityManagerFactory("test_persistence");
	}   
	
	/**
	 * Метод либо создания фабрики, либо получения экземпляра класса с уже созданной фабрикой.
	 * @return экземпляр класса с фабрикой
	 */
	public static Singleton getInstance() {         
		if (instance == null) {
			instance = new Singleton();         
		}         
		return instance;     
	}      
	
	/**
	 * Получение самой фабрики 
	 * @return EntityManagerFactory
	 */
	public EntityManagerFactory getEntityManagerFactory() {         
		return emf;     
	}
	
	/**
	 * Создание интерфейса для работы с БД, а также открытие транзакции
	 * @return EntityManager 
	 */
	public static EntityManager createEMandTrans() {
		EntityManager em = getInstance().getEntityManagerFactory().createEntityManager();
		em.getTransaction().begin();
		return em;
	}
	
	/**
	 * Закрытие интерфейса и транзации
	 * @param em - открытый интерфейс
	 */
	public static void finishEMandTrans(EntityManager em) {
		em.getTransaction().commit();
		em.close();
	}
	
	
}
