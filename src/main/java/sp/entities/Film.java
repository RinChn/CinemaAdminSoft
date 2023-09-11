package sp.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

/**
 * Класс-сущность для таблицы фильмов.
 */
@Entity
@Table(name="film")
public class Film {
	
	/** id фильма в таблице. **/
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id; 
	/** название фильма. **/
	@Column(name="title")
	private String title;
	/** длительность фильма. **/
	@Column(name="length")
	private int length;
	/** дата премьеры фильма. **/
	@Column(name="premiere")
	private String premiere;
	/** ссылка на информацию о режиссере и жанре. **/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "info_about")
	private Info_Film info_about;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "id", cascade = CascadeType.REMOVE)
	private List<Session> allSessions = new ArrayList<>();
	
	
	/**
	* Конструктор класса Film
	*/
	public Film(String title, int length, String premiere, Info_Film dirAndGenre) {
		this.title = title;
		this.length = length;
		this.premiere = premiere;
		this.info_about = dirAndGenre;
	}
	
	/**
	 * Дефолтный конструктор класса.
	 */
	public Film() {
		this.title = "";
		this.length = 0;
		this.premiere = "";
		this.info_about = null;
	}
	
	/**
	 * геттер id фильма.
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/** 
	 * Геттер названия фильма.
	 * @return title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Сеттер названия фильма.
	 * @param new_title - новое название фильма.
	 */
	public void setTitle(String new_title) {
		this.title = new_title;
	}
	
	/**
	 * Геттер длительности фильма.
	 * @return length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Сеттер длительности фильма.
	 * @param new_length - новое значение длительности.
	 */
	public void setLength(int new_length) {
		this.length = new_length;
	}
	
	/**
	 * Геттер даты премьеры фильма.
	 * @return premiere
	 */
	public String getPremiere() {
		return premiere;
	}
	
	/**
	 * Сеттер даты премьеры фильма.
	 * @param new_premiere - новая дата премьеры.
	 */
	public void setPremiere(String new_premiere) {
		this.premiere = new_premiere;
	}
	
	/**
	 * Сеттер информации о фильме (ФИ режиссера и жанр)
	 * @param newInfo - ссылка на новый комплек ФИ режиссера+жанр.
	 */
	public void setInfo(Info_Film newInfo) {
		this.info_about = newInfo;
	}
	
	/**
	 * Геттер информации о фильме (ФИ режиссера и жанр).
	 * @return info_about
	 */
	public Info_Film getInfo() {
		return info_about;
	}
}