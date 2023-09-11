package sp.entities;

import org.apache.log4j.Logger;

import jakarta.persistence.*;
import sp.gui.Repertoire;

/**
 * Класс-сущность для таблицы билетов.
 */
@Entity
@Table(name="ticketsonsession")
public class Ticket {
	/** id сеанса в БД. **/
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;		
	/** ряд **/
	@Column(name="row_numb")
	private final int row_numb;  
	/** номер места **/
	@Column(name="seat")
	private final int seat;  
	/** куплен или нет **/
	@Column(name="bought")
	private int bought; 
	/** id сеанса **/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_session")
	private Session id_session;  
	
	/** Логгер класса **/
	private static final Logger log = Logger.getLogger(Ticket.class.getName());
	
	/**
	 * Конструктор класса Ticket.
	 */
	public Ticket(int row, int seat_number, Session sess) {
		this.row_numb = row;
		this.seat = seat_number;
		this.id_session = sess;
		this.bought = 0;
	}
	
	/**
	 * Дефолтный конструктор класса Ticket.
	 */
	public Ticket() {
		this.row_numb = 0;
		this.seat = 0;
	}
	
	/**
	 * метод покупки билета
	 */
	public void purchase() {
		log.info("Бронирование билета " + row_numb + " ряд " + seat + " на сеанс " 
	+ id_session.getDate() + "." + id_session.getDate() + " в " + id_session.getTime());
		this.bought = 1;
	}
	
	/**
	 * Метод аннулирования покупки билета
	 */
	public void cancel() {
		log.info("Отмена брони билета " + row_numb + " ряд " + seat + " на сеанс " 
	+ id_session.getDate() + "." + id_session.getDate() + " в " + id_session.getTime());
		this.bought = 0;
	}
	
	/**
	 * Методы получения номера и ряда места, информации о брони билета.
	 * @return numb
	 */
	public String[] getNumber() {
		String[] numb = {String.valueOf(row_numb), String.valueOf(seat)};
		return numb;
	}
	
	/**
	 * Геттер состояния билета: продан/нет.
	 * @return bought
	 */
	public int getInfo() {
		return bought;
	}
}
