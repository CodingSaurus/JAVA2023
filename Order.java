import java.io.*;

public class Order extends Menu implements Serializable {
	private static final long serialVersionUID = -1079040423933601253L;
	int orderCount;
	
	//초기화용 생성자
	Order() {}
	
	Order(String menuName, int price) {
		super(menuName, price);
		orderCount =0;
	}
	
	public Order(Menu menu) {
		super(menu.getMenuName(), menu.getPrice());
		orderCount = 0;
	}

	// 메뉴 주문량 접근자
	int getOrderCount() {
		return orderCount;
	}
	
	// 메뉴 주문량 설정자
	void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	
	// 주문량 추가 함수
	void addOrderCount(int n) {
		orderCount += n;
	}
		
	// 주문량 감소 함수
	void minusOrderCount(int n) {
		if (orderCount > n) {
			orderCount -= n;
		}
	}
	
	int getOrderPay() {
		return price * orderCount;
	}

    // equals() 함수 재정의
    public boolean equals(Object ord) {
    	if (!(ord instanceof Order)) //테이블이 아니면
    		return false;
    	Order order = (Order) ord;
    	if (this.menuName.equals(order.getMenuName())) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
	// toString() 함수 재정의
	public String toString() {
		return menuName + " : " + price + " / " +orderCount;
	}
	
	
	//직렬화 아닌 DataOutputStream 이용한 입출력들
	//오더 자기 자신을 기록함.
	public void save_(DataOutputStream dtWriter) throws IOException {
		dtWriter.writeUTF(menuName);
		dtWriter.writeInt(price);
		dtWriter.writeInt(orderCount);
	}		
	
	//오더 자기 자신을 로드함.
	public Order load_(DataInputStream dtLoader) throws IOException {
		menuName = dtLoader.readUTF();
		price = dtLoader.readInt();
		orderCount = dtLoader.readInt();
		return this;
	}
	
	
}