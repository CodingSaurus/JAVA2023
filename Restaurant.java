import java.io.*;
import java.util.ArrayList;

// Restaurant 클래스(super)
public class Restaurant { 
	// Menu 객체들로 이루어진 리스트
	protected ArrayList<Menu> menus = new ArrayList<Menu>();
	// Table 객체들로 이루어진 리스트
	protected ArrayList<Table> tables = new ArrayList<Table>();
	// 매출액 변수
	protected int amount=0;
	
	// Restaurant 기본 생성자 함수
	public Restaurant() {	}
	
	// menus 접근자
	ArrayList<Menu> getMenus() {
		return menus;
	}
	
	// tables 접근자
	ArrayList<Table> getTables() {
		return tables;
	}
	
	// menuLast 접근자
	int getMenuLast() {
		return menus.size();
	}
	
	// tableLast 접근자
	int getTableLast() {
		return tables.size();
	}
	
	// amount 접근자
	int getAmount() {
		return amount;
	}
	
	// 메뉴 객체 탐색
	public int searchMenu(Menu target) {
		int size = menus.size();
		for (int i = 0; i < size; i++) {
			if(menus.get(i).equals((Object)target)) //메뉴 arraylist에 있는 객체와 이름 비교
				return i;
		}
		return -1; //존재하지 않을때는 -1 반환
	} 
		
	// 테이블 객체 탐색
	public int searchTable(Table target) {
		int size = tables.size();
		for (int i = 0; i < size; i++) {
			if(tables.get(i).equals((Object)target)) //메뉴 arraylist에 있는 객체와 이름 비교
				return i;
		}
		return -1; //존재하지 않을때는 -1 반환	
	}
	
	// 메뉴 객체 추가
	public void addMenu(Menu m) throws Exception {
		if (searchMenu(m)==-1) {
			menus.add(m);
		}
		else { 	// 추가하려는 메뉴가 원래 있던 메뉴일 경우
			//수정기능으로 바꿈
			menus.set(searchMenu(m), m);
			//throw new Exception("The menu already exists");
		}
	}
	
	// 테이블 객체 추가
	public void addTable(Table t) throws Exception {
		// 추가하려는 테이블이 원래 없던 테이블일 경우
		if (searchTable(t)==-1) {
			tables.add(t);
		}
		// 추가하려는 테이블이 원래 있던 테이블일 경우
		else {
			tables.set(searchTable(t), t);
			//throw new Exception("The table already exists");
		}
	}
	
	public void addAmount(int paid) {
		amount += paid;
	}
 	
	// 메뉴 객체 삭제
	public void deleteMenu(Menu m) throws Exception{
		if (searchMenu(m) == -1) {
			throw new Exception("The menu does not exist");
		}
		menus.remove(searchMenu(m));	// 삭제한다
		
	}

	// 테이블 객체 삭제
	public void deleteTable(Table t) throws Exception {
		if (searchTable(t) == -1) {
			throw new Exception("The table does not exist");
		}
		tables.remove(searchTable(t));	// 삭제한다
	}
	
	public Order menuToOrder(Menu m) {
		Order ord;
		ord = new Order(m.menuName, m.price);
		return ord;
	}
	
	// 계산
	public int pay(Table t) {
		int paid = t.outTable();
		addAmount(paid);
		return paid;
	}
	
	//UI에서 사용하는 JTable 사용을 위해.
	public Object[][] uiUseTable() {
		Object[][] array = new Object[tables.size()][3];
		
		for (int i = 0; i < tables.size(); i++) {
			array[i][0] = i+1;
			array[i][1] = tables.get(i).getTableName();
			array[i][2] = String.valueOf(tables.get(i).getMember());
		}
		
		return array; //이 이중 배열을 사용함
	}
	
	
	public Object[][] uiUseMenu() {
		Object[][] array = new Object[menus.size()][3];
		
		for (int i = 0; i < menus.size(); i++) {
			array[i][0] = i+1;
			array[i][1] = menus.get(i).getMenuName();
			array[i][2] = String.valueOf(menus.get(i).getPrice());
		}
		
		return array; //이 이중 배열을 사용함
	}
	
	public Object[][] uiUseOrder(int index) {
		
		Table table = tables.get(index);
		Object[][] array = new Object[table.getOrderLast()][4];
		
		array = table.uiUseOrder();
		
		return array; //이 이중 배열을 사용함
	}
	
	
	// !!직렬화 버전!!
	//.dat 파일 생성
	public void save(FileOutputStream writer, DataOutputStream dtWriter, ObjectOutputStream obWriter) throws IOException {
			
			//분명 User에서 잘 닫아줬는데 왜 이렇게 출력되는지 모르겠습니다
			writer = new FileOutputStream("restaurant.dat");
			dtWriter = new DataOutputStream(writer);
			obWriter = new ObjectOutputStream(writer);
			
			obWriter.writeObject(menus);
			obWriter.writeObject(tables);
			dtWriter.writeInt(amount);
	}
	
	// !!직렬화 버전!!
	//가져오기
	@SuppressWarnings("unchecked")
	public Restaurant load(Restaurant newBab, FileInputStream loader, DataInputStream dtLoader, ObjectInputStream obLoader) throws IOException, ClassNotFoundException {
		try {
				loader = new FileInputStream("restaurant.dat");
				dtLoader = new DataInputStream(loader);
				obLoader = new ObjectInputStream(loader);
				
				newBab.menus = (ArrayList<Menu>)obLoader.readObject();
				newBab.tables = (ArrayList<Table>)obLoader.readObject();
				newBab.amount = dtLoader.readInt();
				
				//마찬가지로 잘 닫았는데 왜 이렇게 출력되는지 모르겠습니다
				return newBab;
		}
		
		catch (FileNotFoundException e) {
			System.out.println("[알림] 환영합니다! 처음 사용하시는군요.");
			return newBab;
		}
		
	}
	
	//DataOutputStream 입출력
	
	//Menus 배열 저장해주는 함수인데, menu 각각은 Menu 클래스에서 저장 될 것임
	public void saveMenu_(ArrayList<Menu> menus, DataOutputStream dtWriter) throws IOException {
		for(int i = 0; i < menus.size(); i++) {	
			menus.get(i).save_(dtWriter); //Menu 클래스의 함수 사용해서 저장
		}
	}
		
	//Table 배열 저장해주는 함수인데, Table 각각은 Table 클래스에서 저장 될 것임
	public void saveTable_(ArrayList<Table> tables, DataOutputStream dtWriter) throws IOException {
		for(int i = 0; i < tables.size(); i++) {	
			tables.get(i).save_(dtWriter); //Table 클래스의 함수 사용
		}
	}
		
	//Menus 배열을 불러와주는 함수인데, Menu 각각은 메뉴 클래스에서 불러와짐.
	public ArrayList<Menu> loadMenu_(ArrayList<Menu> menus, int menuLast, DataInputStream dtLoader) throws IOException {
		menus.clear(); //arraylist 초기화
		for(int i = 0; i < menuLast; i++ ) { //menuLast는 dat에서 가져온 값
			menus.add(new Menu().load_(dtLoader));
		}
		return menus;
	}
		
	//Table 배열을 불러와주는 함수인데, Table 각각은 테이블 클래스에서 불러와짐.
	public ArrayList<Table> loadTable_(ArrayList<Table> tables, int tableLast, DataInputStream dtLoader) throws IOException {
		tables.clear(); //arraylist 초기화
		for(int i = 0; i < tableLast; i++ ) { //tableLast는 dat에서 가져온 값
			tables.add(new Table().load_(dtLoader)); //Table 클래스의 함수 사용
		}
		return tables;
	}
	
	
	public void save_(FileOutputStream writer, DataOutputStream dtWriter) throws IOException {
		writer = new FileOutputStream("restaurant.dat");
		dtWriter = new DataOutputStream(writer);
			
		//1. 메뉴개수 int로 받아오기.
		dtWriter.writeInt(menus.size());
		//메뉴 클래스들 저장
		saveMenu_(menus, dtWriter);
		//2. 테이블개수 int로 받아오기
		dtWriter.writeInt(tables.size());
		//테이블 개수만큼 반복. 테이블 클래스 저장. 안에 order 포함.
		saveTable_(tables, dtWriter);
		//3. 총액 int로 저장하기
		dtWriter.writeInt(amount);
	}
		
	//가져오기
	public Restaurant load_(Restaurant newBab, FileInputStream loader, DataInputStream dtLoader) throws IOException, FileNotFoundException {
		loader = new FileInputStream("restaurant.dat");
		dtLoader = new DataInputStream(loader);
		
		//1. 메뉴개수 int로 불러오기
		int tempMenuIndex = dtLoader.readInt();
		//메뉴 클래스들 불러오기
		newBab.menus = loadMenu_(newBab.menus, tempMenuIndex, dtLoader);
		//2. 테이블개수 int로 불러오기
		int tempTableIndex = dtLoader.readInt();			//테이블 개수만큼 반복. 테이블 클래스 저장. 안에 order 포함.
		newBab.tables = loadTable_(newBab.tables, tempTableIndex, dtLoader);
		//3. 총액 int로 불러오기
		newBab.amount = dtLoader.readInt();
		return newBab;
	}
	
	
	
	
}
