package sp.back;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import sp.entities.Film;
import sp.entities.Info_Film;
import sp.entities.Session;

/**
 * Класс, содержащий методы операций с таблицами базы данных.
 */
public class OperationsWithDatabase {	
	/** Логгер класса **/
	private static final Logger log = Logger.getLogger(OperationsWithDatabase.class.getName());
	
	/**
	 * Создание сеанса с указанными значениями полей.
	 * @param numbMonth - номер месяца сеанса.
	 * @param day - день проведения сеанса.
	 * @param time - время проведения сеанса.
	 * @param movie - фильм добавляемого сеанса.
	 */
	public static void createSessions(int numbMonth, int day, String time, Film movie) {
		log.info("Создание сеанса на " + day + "." + numbMonth + " в " + time + ".");
		EntityManager em = Singleton.createEMandTrans();
		Session newSess = new Session(numbMonth, day, time, movie);
		em.persist(newSess);
		newSess.fill_tckts(em);
		Singleton.finishEMandTrans(em);
	}
	
	/**
	 * Добавление фильма в список базы данных.
	 * @param title - название фильма.
	 * @param length - длительность фильма
	 * @param premiere - дата премьеры фильма.
	 * @param info - ссылка на элемент Info_Film.
	 * @return newMovie - добавленный фильм.
	 */
	public static Film addMovieInDatabase(String title, String length, String premiere, Info_Film info) {
		log.info("Добавление фильма " + title + " от " + info.getDirName() + " " + info.getDirLastName() + " в базу данных.");
		EntityManager em = Singleton.createEMandTrans();
		Film newMovie = new Film(title, Integer.valueOf(length), premiere, info);
		em.persist(newMovie);
		Singleton.finishEMandTrans(em);
		return newMovie;
	}
	
	/**
	 * Удаление всех сеансов заданного месяца.
	 * @param monthNumb - номер месяца, сеансы которого будут удалены.
	 */
	public static void deleteAllSessions(int monthNumb) {
		log.info("Удаление всех сеансов в " + HelperMonth.GenitiveCase(monthNumb));
		EntityManager em = Singleton.createEMandTrans();
		Query sessions =  em.createQuery("DELETE FROM Session e WHERE e.numb_month = :numb_month");
		System.out.println(monthNumb);
		sessions.setParameter("numb_month", monthNumb);
		sessions.executeUpdate();
		Singleton.finishEMandTrans(em);
	}
	
	/**
	 * Конвертирование информации в виде имени режиссера и жанра в экземпляр класса-сущности Info_Film.
	 * @param nameDir - ФИ режиссера.
	 * @param genre - жанр.
	 * @return - созданный или найденный экземпляр класса.
	 */
	public static Info_Film convertInfo(String nameDir, String genre) {
		// Получение списка всех элементов из соответствующей таблицы БД.
		EntityManager em = Singleton.createEMandTrans();
		List<Info_Film> listInfo = em.createQuery("SELECT u FROM Info_Film u").getResultList();
		Info_Film dirAndGenre = null;
		
		String[] names = nameDir.split(" ");
		String lastName = "";
		String name = names[0];
		for (int i = 1; i < names.length; i++) {
			lastName += names[i] + " ";
		}
		// Удаляется последний пробел.
		lastName = lastName.substring(0, lastName.length() - 1);
		
		// Поиск по элементам таблицы. Если найден хотя бы подходящий режиссер,
		// то элементы lastName и name заполняются аналогично тому, как заполнены в таблице 
		// (на случай неповторения при двойных именах/фамилиях).
		for (Info_Film elem : listInfo) {
			if (nameDir.equals(elem.getDirName() + " " + elem.getDirLastName()) && 
					genre.equals(elem.getGenre())) {
				dirAndGenre = elem; 
				break;
			} else if (nameDir.equals(elem.getDirName() + " " + elem.getDirLastName())) {
				lastName = elem.getDirLastName();
				name = elem.getDirName();
			}
		}
		if (dirAndGenre == null) {
			log.info("Создание элемента 'Информация о фильме' в виде комбинации " + nameDir + " " + genre);
			dirAndGenre = new Info_Film(name, lastName, genre);
			em.persist(dirAndGenre);
		}
		Singleton.finishEMandTrans(em);
		return dirAndGenre;
	}
	
	/**
	 * Поиск сеанса по заданным параметрам.
	 * @param numbMonth - номер месяца
	 * @param day - день сеанса
	 * @param time - время сеанса
	 * @param titleFilm - название фильма
	 * @return найденный сеанс.
	 */
	public static Session searchSession(int numbMonth, int day, String time, String titleFilm) {
		log.info("Поиск сеанса на " + day + "." + numbMonth + " в " + time + " с фильмом " + titleFilm + ".");
		EntityManager em = Singleton.createEMandTrans();
		Query qSess = em.createQuery("SELECT u FROM Session u WHERE u.numb_month = :numb_month AND u.date = :date "
				+ "AND u.time = :time AND u.film = :film");
		qSess.setParameter("numb_month", numbMonth);
		qSess.setParameter("date", day);
		qSess.setParameter("time", time);
		Query movie = em.createQuery("SELECT e FROM Film e WHERE e.title =:title");
		movie.setParameter("title", titleFilm);
		Film film = (Film) movie.getResultList().get(0);
		qSess.setParameter("film", film);
		Session result = (Session) qSess.getResultList().get(0);
		return result;
	}
	
	/** 
	 * Получение отсортированного списка режиссеров.
	 * @return список режиссеров.
	 */
	public static String[] getSortListDirectors() {
		
		EntityManager em = Singleton.createEMandTrans();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Info_Film> cq = cb.createQuery(Info_Film.class);
		Root<Info_Film> root = cq.from(Info_Film.class);
		cq.orderBy(cb.asc(root.get("name")));
		List<Info_Film> allInfo = em.createQuery(cq).getResultList();
		Singleton.finishEMandTrans(em);
		
		List<String> directorsList = new ArrayList<String>();
		for (Info_Film inf : allInfo) 
			directorsList.add(inf.getDirName() + " " + inf.getDirLastName());
		
		directorsList = new ArrayList<>(new HashSet<>(directorsList));
		directorsList = new ArrayList<>(new HashSet<>(directorsList));
		
		String[] directors = directorsList.toArray(new String[directorsList.size()]);
		Arrays.sort(directors);
		log.info("Получение из БД отсортированного списка уникальных ФИ режиссеров длиной " + directors.length);
		return directors;
	}
	
	/**
	 * Получение отсортированного списка жанров.
	 * @return список жанров.
	 */
	public static String[] getSortListGenres() {
		EntityManager em = Singleton.createEMandTrans();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Info_Film> cq = cb.createQuery(Info_Film.class);
		Root<Info_Film> root = cq.from(Info_Film.class);
		cq.orderBy(cb.asc(root.get("genre")));
		List<Info_Film> allInfo = em.createQuery(cq).getResultList();
		Singleton.finishEMandTrans(em);
		
		List<String> genresList = allInfo.stream().map(Info_Film::getGenre).collect(Collectors.toList());
		genresList = new ArrayList<>(new HashSet<>(genresList));
		
		String[] genres = genresList.toArray(new String[genresList.size()]);
		Arrays.sort(genres);
		log.info("Получение из БД отсортированного списка уникальных жанров длиной " + genres.length);
		return genres;
	}
	
	/**
	 * Метод получения списка фильмов по заданным параметрам.
	 * @param title - название
	 * @param length - длительность
	 * @param premiere - премьера
	 * @param dirAndGenre - соответствующий элемент Info_Film
	 * @return arrMovies - список найденных фильмов 
	 */
	public static List<Film> getMovie(String title, int length, String premiere, Info_Film dirAndGenre) {
		EntityManager em = Singleton.createEMandTrans();
		List<Film> arrMovies = null;
		Query requestMovie = em.createQuery("SELECT u FROM Film u WHERE u.title = :title AND u.length = :length AND u.premiere = :premiere AND u.info_about = :info_about");
		requestMovie.setParameter("title", title);
		requestMovie.setParameter("length", length);
		requestMovie.setParameter("premiere", premiere);
		requestMovie.setParameter("info_about", dirAndGenre);
		arrMovies = requestMovie.getResultList();
		Singleton.finishEMandTrans(em);
		log.info("Получение фильма с названием " + title + " от " + dirAndGenre.getDirName() + " " + dirAndGenre.getDirLastName() + ".");
		return arrMovies;
	}
	
	/**
	 * Получение отсортированного списка сеансов на указанный месяц.
	 * @param monthNumb - номер искомого месяца.
	 * @return список сеансов.
	 */
	public static List<Session> getSortSessionsMonth(int monthNumb) {
		EntityManager em = Singleton.createEMandTrans();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Session> cq = cb.createQuery(Session.class);
		Root<Session> root = cq.from(Session.class);
		cq.where(cb.equal(root.get("numb_month"), monthNumb));
		cq.orderBy(cb.asc(root.get("date")));
		
		List<Session> allSessions = em.createQuery(cq).getResultList();
		
		Singleton.finishEMandTrans(em);
		log.info("Получение отсортированного по дню демонстрации списка сеансов в " + HelperMonth.GenitiveCase(monthNumb));
		return allSessions;
	}
}
