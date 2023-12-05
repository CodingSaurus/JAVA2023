//조희수 학우 코드 사용
import java.io.*;
import java.util.*;

public class User {
	public static void main(String args[]){
		User user = new User();
		Scanner input = new Scanner(System.in);	// 스캐너 객체 호출
		Restaurant bab = new Restaurant();	// 식당 객체 생성하고 참조 변수가 가리키게 함
		//파일 입출력
		FileOutputStream writer = null;
		DataOutputStream dtWriter = null;
		ObjectOutputStream obWriter = null;
		FileInputStream loader = null;
		DataInputStream dtLoader = null;
		ObjectInputStream obLoader = null;
		
		//시작하면 기존에 저장된 설정을 불러온다. 존재하지 않는 파일에 대한 경우 함수 내부에서 exception 발생
		try {
			bab = bab.load(bab, loader, dtLoader, obLoader);
		} 
		catch (FileNotFoundException ne) {
			System.out.println("[알림] 환영합니다! 처음 사용하시는군요.");
			bab = new Restaurant(); //초기화
		} catch (IOException e1) {
			System.out.println("[오류] 파일을 정상적으로 읽을 수 없습니다.");
			input.close();
			System.exit(0);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		// while문을 통한 반복
		while(true) {
			ArrayList<Table> tables = bab.getTables();
			ArrayList<Menu> menus = bab.getMenus();
			System.out.println("무엇을 하시겠습니까?(선택지 번호로 입력해주세요.)");
			System.out.println("1.주문 관리, 2.메뉴 관리, 3.테이블 관리, 4. 현재 내역 저장하기, 5. 불러오기, 6.종료");
			int what = input.nextInt();
			if(what==1 && (bab.getTableLast() == 0 || bab.getMenuLast() == 0)) {
				System.out.println("메뉴와 테이블을 먼저 추가해주세요.");
				continue;
			}
			switch(what) {
				case 1:	//주문관리
					System.out.println("1. 주문 추가, 2. 주문 내역 확인, 3. 계산");
					int what1 = input.nextInt();
					if (what1==1) {
						user.table(bab);
						System.out.println("어느 테이블입니까?");
						int table = input.nextInt();
						tables.get(table).inTable();
						user.menu(bab); 
						System.out.println("주문하실 메뉴를 선택해주세요.");
						int order = input.nextInt();
						System.out.println("몇 개 주문하시겠습니까?");
						int orderN = input.nextInt();	
						Order ord = bab.menuToOrder(menus.get(order));
						try {
							tables.get(table).addOrder(ord, orderN);
						} catch (ArrayIndexOutOfBoundsException aiobe) {
							System.out.println("orders[] is full."); 
						}	
					}
					else if (what1==2) {
						user.table(bab);
						System.out.println("어느 테이블입니까?");
						int table = input.nextInt();
						user.order(tables.get(table));
					}
					else if (what1==3) {
						user.table(bab);
						System.out.println("어느 테이블입니까?");
						int table = input.nextInt();
						int paid = bab.pay(tables.get(table));
						System.out.println(paid+"가 결제 되었습니다.");
					}
					break;
					
				case 2:	// 메뉴 관리
					System.out.println("1. 메뉴 추가, 2. 메뉴 삭제");
					int what2 = input.nextInt();
					if (what2==1) {
						input.nextLine();
						System.out.println("추가하실 메뉴의 이름을 입력하세요.");	
						String menuName = input.nextLine();	// 메뉴명 입력 받음
						System.out.println("추가하실 메뉴의 가격을 입력하세요.");
						int menuPrice = input.nextInt();	// 메뉴 가격 입력 받음
						try {
							bab.addMenu(new Menu(menuName, menuPrice));	// 메뉴 객체 생성 후 식당 메뉴에 추가
							System.out.println("\'"+menuName+"\'"+"(이/가) 추가되었습니다.");
						} catch (ArrayIndexOutOfBoundsException aiobe) {
							System.out.println("menus[] is full."); 
						} catch (Exception e) {	
							System.out.println(e.getMessage());
						}
					}
					else if (what2==2) {
						input.nextLine();
						System.out.println("삭제하실 메뉴의 이름을 입력하세요.");	
						String menuName = input.nextLine();	// 메뉴명 입력 받음
						try {
							bab.deleteMenu(new Menu(menuName));	// 코드와 이름을 부여받은 임의의 메뉴 객체를 생성하여 같은 메뉴 객체가 있다면 삭제
							System.out.println(menuName+"이 삭제되었습니다.");
						} catch (Exception e) {	
							System.out.println(e.getMessage());
						}
					}
					break;
					
				case 3:	// 테이블 관리
					System.out.println("1. 테이블 추가, 2. 테이블 삭제");
					int what3 = input.nextInt();
					if (what3==1) {
						input.nextLine();
						System.out.println("추가하실 테이블의 이름을 입력하세요.");	
						String tableName = input.nextLine();	// 테이블명 입력 받음
						System.out.println("추가하실 테이블의 수용 가능 인원을 입력하세요.");	
						int member = input.nextInt();	// 수용 가능 인원 입력 받음
						System.out.println("추가하실 테이블의 이용 가능 여부를 입력하세요.(true/false)");	
						boolean available = input.nextBoolean();
						try {
							bab.addTable(new Table(tableName, member, available));	// 테이블 객체 생성 후 식당 테이블에 추가
							System.out.println(tableName+" 테이블이 추가되었습니다.");
						} catch (ArrayIndexOutOfBoundsException aiobe) {
							System.out.println("tables[] is full."); 
						} catch (Exception e) {	
							System.out.println(e.getMessage());
						}
					}
					else if (what3==2) {
						input.nextLine();
						System.out.println("삭제하실 테이블의 이름을 입력하세요.");	
						String tableName = input.nextLine();	// 테이블명 입력 받음
						try {
							bab.deleteTable(new Table(tableName));	// 이름을 부여받은 임의의 테이블 객체 생성 후 같은 이름의 테이블 객체 있다면 삭제
							System.out.println(tableName+" 테이블이 삭제되었습니다.");
						} catch (Exception e) {	
							System.out.println(e.getMessage());
						}
					}
					break;	
					
				case 4: // 수행 데이터 저장하기
					System.out.println("지금까지의 내역을 저장합니다.");
					try { //저장 해주는 Restaurant의 함수 호출
						bab.save(writer, dtWriter, obWriter);
					}
					catch (IOException ioe) { //읽기 중 에러 발생시
						System.out.println("[오류] 저장에 실패했습니다.");
						ioe.printStackTrace();
					}
					finally {
						user.closeOutputStream(writer, dtWriter, obWriter);
						System.out.println("작업을 완료했습니다.");
					}
					break;
					
				case 5: // 파일 불러오기
					System.out.println("저장된 파일을 불러옵니다. 기존 테이블 정보는 모두 초기화됩니다.");
					
					try { //로드 해주는 Restaurant의 함수 호출
						bab = bab.load(bab, loader, dtLoader, obLoader);
					}
					catch (IOException ioe) { //읽기 중 에러 발생시
						System.out.println("[오류] 파일을 정상적으로 읽을 수 없습니다.");
						ioe.printStackTrace();
						System.exit(0);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finally {
						user.closeInputStream(loader, dtLoader, obLoader);
						System.out.println("작업을 완료했습니다.");
					}
					break;
					
					
				case 6: // 종료 선택 시 while문 종료 및 프로그램 종료
					System.out.println("매출: "+ bab.getAmount());
					System.out.println("프로그램을 종료합니다.");
					input.close();
					System.exit(0);
			}	
		}
	}
	
	
	// menu판 함수
	private void menu(Restaurant r) {
		ArrayList<Menu> menus= r.getMenus();
		String menu = "----------메뉴판----------\n";
		//반복문을 사용해 식당의 메뉴를 menu 변수에 추가
		for(int i=0; i<r.getMenuLast(); i++) {
			if (menus.get(i) != null) {
				menu += i +". "+ menus.get(i)+"원";
			}
			else {
				break;
			}
			menu += "\n";
		}
		System.out.println(menu);
	}
	
	// table 나열 함수
	private void table(Restaurant r) {
		ArrayList<Table> tables= r.getTables();
		String table = "----------테이블----------\n";
		//반복문을 사용해 식당의 메뉴를 table 변수에 추가
		for(int i=0; i<r.getTableLast(); i++) {
			if (tables.get(i) != null) {
				table += i +". "+ tables.get(i);
			}
			else {
				break;
			}
			table += "\n";
		}
		System.out.println(table);
	}
	
	// table 나열 함수
	private void order(Table t) {
		ArrayList<Order> orders= t.getOrders();
		String order = "----------주문내역----------\n";
		//반복문을 사용해 식당의 메뉴를 table 변수에 추가
		for(int i=0; i<t.getOrderLast(); i++) {
			if (orders.get(i) != null) {
				order += i +". "+ orders.get(i)+"개";
				}
			else {
				break;
			}
			order += "\n";
		}
		order += "------------------------\n";
		order += "주문금액: " + t.getTotal();
		System.out.println(order);
	}
	
	//OutputStream 닫기용 함수
	private void closeOutputStream(FileOutputStream writer, DataOutputStream dtWriter, ObjectOutputStream obWriter) {
		try {
			dtWriter.close();
			obWriter.close(); 
			writer.close();
		} catch (IOException e) {

		} catch (NullPointerException e) {
			//닫혀 있을 때 또 닫는 경우
		} 
	}
	
	//InputStream 닫기용 함수
	private void closeInputStream(FileInputStream loader, DataInputStream dtLoader, ObjectInputStream obLoader) {
		try {
			dtLoader.close();
			obLoader.close(); 
			loader.close();
		} catch (IOException e) {

		} catch (NullPointerException e) {
			//닫혀 있을 때 또 닫는 경우
		} 
	}
	
}
