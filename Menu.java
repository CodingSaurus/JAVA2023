import java.io.*;

// Menu 클래스
public class Menu implements Serializable { 
	
	private static final long serialVersionUID = 1421313881209557959L;
	protected String menuName = "";	// 메뉴명 변수
	protected int price = 0;	// 메뉴 가격 변수
	
	//메뉴 생성자 : 초기화용
	Menu() { }
	
	//메뉴 생성자: 이름만 지정
	Menu(String menuName){
		this.menuName = menuName;
	}
	
	//메뉴 생성자: 이름과 가격을 지정하여 생성함
	Menu(String menuName, int price) {
		this.menuName = menuName;
		this.price = price;
	}
		
	// 메뉴명 접근자
	String getMenuName() {
		return menuName;	// 메뉴명 반환
	}
		
	// 메뉴 가격 접근자
	int getPrice() {
		return price;	//메뉴 가격 반환
	}
			
	// 메뉴명 설정자
	void setMenuName(String menuName) {
		this.menuName = menuName;
	}
		
	// 메뉴 가격 설정자
	void setPrice(int price) {
		this.price = price;
	}
	
	
	// equals() 함수 재정의
    public boolean equals(Object m) {
    	if (!(m instanceof Menu)) //메뉴가 아니면
    		return false;
    	Menu menu = (Menu) m;
    	if (this.menuName.equals(menu.getMenuName())) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    
	// toString() 함수 재정의
	public String toString() {
		return menuName + " : " + price ;
	}
	
	
	//직렬화 아닌 DataOutputStream 이용한 입출력들
	//메뉴 자기 자신을 기록함.
	public void save_(DataOutputStream dtWriter) throws IOException {
		dtWriter.writeUTF(menuName);
		dtWriter.writeInt(price);
	}
		
	//메뉴 자기 자신을 불러옴.
	public Menu load_(DataInputStream dtLoader) throws IOException {
		menuName = dtLoader.readUTF();
		price = dtLoader.readInt();
		return this;
	}
	
	
}
