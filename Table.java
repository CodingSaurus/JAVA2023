import java.io.*;
import java.util.ArrayList;

// Table 클래스
public class Table implements Serializable {

	private static final long serialVersionUID = 7962644182657831011L;
	private String tableName;	// 테이블 번호 변수
	private int member=0;	// 수용 가능 인원 변수
	private boolean available = true;	// 테이블 이용 가능 여부 변수
	private ArrayList<Order> orders = new ArrayList<Order>();	// 주문 내역 리스트 변수
	private int orderLast = 0;	// orders 리스트에서 주문이 채워진 마지막 인덱스의 다음 인덱스를 확인하기 위한 변수
	
	// 테이블 생성자 : 초기화용
	Table() {	}
	
	// 테이블 생성자
	Table(String tableName){
		this.tableName = tableName;
	}
		
	// 테이블 생성자
	Table(String tableName, int member, boolean available){
		this.tableName = tableName;
		this.member = member;
		this.available = available;
	}
			
	// 테이블명 접근자
	String getTableName() {
		return tableName;
	}
	
	// 수용 가능 인원 접근자
	int getMember() {
		return member;
	}
	
	// 이용 가능 여부 접근자
	boolean getAvailable() {
		return available;
	}
	
	// orderLast 접근자
	int getOrderLast() {
		return orders.size();
	}
		
	// 테이블명 설정자
	void setName(String tableName) {
		this.tableName = tableName;
	}
	
	// 수용 가능 인원 설정자
	void setMember(int member) {
		this.member = member;
	}
	
	// 이용 가능 여부 설정자
	void setAvailable(boolean tf) {
		this.available = tf;
	}
			
	// 주문 추가 함수
	void addOrder(Order order, int n){
		int search = searchOrder(order);
		// 이전에 주문했던 메뉴가 아닐 경우
		if(search == -1) {
			orders.add(order);	// orderLast 인덱스에 새로운 주문을 추가합니다.
			search = orderLast;	// search 값을 order 객체가 들어간 인덱스 값으로 바꿉니다.
		}
		orders.get(search).addOrderCount(n);	// 주문한 해당 메뉴의 주문량을 n 증가합니다.
		orderLast = orders.size();
	}
	
	// 메뉴 객체 탐색
	public int searchOrder(Order target) {
		int size = orders.size();
		for (int i = 0; i < size; i++) {
			if(orders.get(i).equals((Object)target)) //메뉴 arraylist에 있는 객체와 이름 비교
				return i;
		}
		return -1; //존재하지 않을때는 -1 반환	
	}
	
	// 테이블에 손님이 들어온 경우
	int inTable() {
		if (available == true) {
			available = false;	// 이용 불가능으로 변경
			return 0;
		}
		else {
			return -1;
		}
	}
	
	// 손님이 계산을 마치고 나갈 경우
	int outTable() {
		int paid = getTotal();	// paid 변수에 total 값을 대입(total은 0으로 초기화할 것이기 때문에)
		orders = new ArrayList<Order>(); //오더 리스트 초기화
		available = true;	// 이용 가능으로 변경	 
		orderLast = 0;	// orderLast를 0으로 변경
		return paid;
	}
	
	// 주문 내역을 보여주는 함수
	ArrayList<Order> getOrders() {
		return orders;
	}
	
	// total 접근자
	int getTotal() {
		int total = 0;
		for (int i=0; i < orders.size(); i++) {
			total += orders.get(i).getOrderPay();
		}
		return total;
	}
	
	// equals() 함수 재정의
    public boolean equals(Object t) {
    	if (!(t instanceof Table)) //테이블이 아니면
    		return false;
    	Table table = (Table) t;
    	if (this.tableName.equals(table.getTableName())) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
	// toString() 재정의
	public String toString() {
		return tableName+"("+member+"): " + available;
	}
		
	//직렬화 입출력
	private void writeObject (ObjectOutputStream obWriter) throws IOException, ClassNotFoundException {
		obWriter.defaultWriteObject();
		obWriter.writeObject(orders);
	}
	
	private void readObject (ObjectInputStream obLoader) throws IOException, ClassNotFoundException {
		obLoader.defaultReadObject();
		orders = (ArrayList<Order>)obLoader.readObject();
	}
	
	
	
	//DataOutputStream 입출력
	//테이블 자기 자신을 기록함. Order도 여기서 넘겨 처리.
	public void save_(DataOutputStream dtWriter) throws IOException {
		dtWriter.writeUTF(tableName);
		dtWriter.writeInt(member);
		dtWriter.writeBoolean(available);
		dtWriter.writeInt(orderLast);
		//order 저장	
		for(int i = 0; i < orderLast; i++) { //order 개수만큼 반복.
			orders.get(i).save_(dtWriter); //order 클래스 저장.
		}
	}
		
	//테이블 자기 자신을 불러옴. Order도 여기서 넘겨 처리.
	public Table load_(DataInputStream dtLoader) throws IOException {
		//새 테이블 객체 생성
		tableName = dtLoader.readUTF(); 
		member = dtLoader.readInt(); 
		available = dtLoader.readBoolean();
		orderLast = dtLoader.readInt(); //dat에서 가져온 값들
			
		orders.clear(); //초기화
		//order 로드
		for(int i = 0; i < orderLast; i++) { //order 개수만큼 반복.
			orders.add(new Order().load_(dtLoader)); //order 클래스 불러오기.
		}
		return this;
	}
	
	public Object[][] uiUseOrder() {
		
		Object[][] array = new Object[orders.size()][4];
		
		for (int i = 0; i < orders.size(); i++) {
			array[i][0] = orders.get(i).getMenuName();
			array[i][1] = String.valueOf(orders.get(i).getPrice());
			array[i][2] = String.valueOf(orders.get(i).getOrderCount());
			array[i][3] = String.valueOf(orders.get(i).getOrderPay());
		}
		
		return array; //이 이중 배열을 사용함
	}

	
}

