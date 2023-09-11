package sp.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import sp.back.Singleton;


/**
 * Класс-сущность для таблицы сеансов.
 */
@Entity
@Table(name="sessions")
public class Session {
	
	/** Общее количество билетов **/
	private final static int COUNT_ROW = 6;
	private final static int COUNT_SEAT = 14;
	
	/** id сеанса в БД. **/
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** месяц сеанса **/
	@Column(name="numb_month")
	private int numb_month;
	
	/** день сеанса **/
	@Column(name="date")
	private int date;
	
	/** время сеанса **/
	@Column(name="time")
	private String time;
	
	/** демонстрируемый фильм **/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "film")
	private Film film;
	
	/** Массив билетов на сеанс **/
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "id_session", cascade = CascadeType.REMOVE)
	private List<Ticket> allTickets = new ArrayList<>();
		
	/**
	* Конструктор класса Session.
	*/
	public Session(int numbMonth, int date, String time, Film film) {
		this.numb_month = numbMonth;
		this.date = date;
		this.time = time;
		this.film = film;
	}
	
	/** 
	 * Дефолтный конструктор класса.
	 */
	public Session() {
		this.date = 0;
		this.time = "";
		this.film = null;
	}
		
	/**
	* Метод заполнения массива билетов на сеанс.
	*/
	public void fill_tckts(EntityManager em) {
		for (int i = 1; i <= COUNT_ROW; i++)
			for (int j = 1; j <= COUNT_SEAT; j++)
				em.persist(new Ticket(i, j, this));
	}
	
	/**
	 * Геттер номера месяца сеанса.
	 * @return numb_month
	 */
	public int getMonth() {
		return numb_month;
	}
	
	/**
	 * Геттер номера месяца сеанса.
	 * @return date
	 */
	public int getDate() {
		return date;
	}
	
	/**
	 * Сеттер даты сеанса.
	 * @param new_date - новая дата.
	 */
	public void setDate(int new_date) {
		this.date = new_date;
	}
	
	/**
	 * Геттер времени проведения сеанса.
	 * @return time
	 */
	public String getTime() {
		return time;
	}
	
	/**
	 * Сеттер времени сеанса.
	 * @param new_time - новое время сеанса.
	 */
	public void setTime(String new_time) {
		this.time = new_time;
	}
	
	/**
	 * Геттер фильма.
	 * @return film
	 */
	public Film getFilm() {
		return film;
	}
	
	/**
	 * Сеттер фильма.
	 * @param new_film - новый фильм.
	 */
	public void setFilm(Film new_film) {
		this.film = new_film;
	}
	
	/**
	 * Бронирование билета на сеанс по ряду и месту.
	 * @param row - ряд
	 * @param seat_number - место
	 */
	public void bookTick(int row, int seat_number) {
		EntityManager em = Singleton.createEMandTrans();
		Ticket one = allTickets.get((row - 1)*COUNT_SEAT + seat_number - 1);
		one.purchase();
		em.merge(one);
		Singleton.finishEMandTrans(em);
	}
	
	/**
	 * Отмена брони билета на сеанс по ряду и месту.
	 * @param row - ряд.
	 * @param seat_number - место.
	 */
	public void freeTick(int row, int seat_number) {
		EntityManager em = Singleton.createEMandTrans();
		Ticket one = allTickets.get((row - 1)*COUNT_SEAT + seat_number - 1);
		one.cancel();
		em.merge(one);
		Singleton.finishEMandTrans(em);
	}
	
	/**
	 * Геттер списка билетов на сеанс.
	 * @return allTickets - список билетов на сеанс.
	 */
	public List<Ticket> getTckts() {
		return allTickets;
	}
	
}
