import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import java.awt.Font;

import javax.swing.JOptionPane;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JSpinner;
import javax.swing.JTable;

import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.JList;

public class UI {

	private JFrame frame;
	//레스토랑 클래스
	Restaurant bab = new Restaurant();
	//파일 입출력
	FileOutputStream writer = null;
	DataOutputStream dtWriter = null;
	ObjectOutputStream obWriter = null;
	FileInputStream loader = null;
	DataInputStream dtLoader = null;
	ObjectInputStream obLoader = null;
	//버튼들
	JButton btnQuick_order = new JButton("메뉴 주문하기");
	JButton btnQuick_checkout = new JButton("테이블 계산하기");
	JButton btnQuick_amount = new JButton("매출 조회");
	JButton btnQuick_save = new JButton("현재 상태 저장");
	JButton btnQuick_off = new JButton("프로그램 종료");
	JButton btnAdmin = new JButton("관리자...");
	//라벨
	JLabel tableInfo_titleLabel = new JLabel("테이블 예시1 주문 내역 ");
	//테이블 관련
	String colNames[] = {"메뉴명", "단가", "수량", "금액"};
	Object data[][] = {};
	public DefaultTableModel orderModel = new DefaultTableModel(data, colNames);
	JTable tableOnInfo;
	//주문 관련
	ArrayList<Integer> orderTemp; //버튼 클릭하면 인덱스들이 여기 저장됨
	
	//화면 전환에 사용하는 인덱스들
	int tableIndex = -1;
	int menuIndex = -1;
	
	
	private void button_Invisible() {
		btnQuick_order.setVisible(false);
		btnQuick_checkout.setVisible(false);
		btnQuick_amount.setVisible(false);
		btnQuick_save.setVisible(false);
		btnQuick_off.setVisible(false);
		btnAdmin.setVisible(false);
	}
	
	private void button_Visible() {
		btnQuick_order.setVisible(true);
		btnQuick_checkout.setVisible(true);
		btnQuick_amount.setVisible(true);
		btnQuick_save.setVisible(true);
		btnQuick_off.setVisible(true);
		btnAdmin.setVisible(true);
	}
	
	private void setTableIndex(int i) {
		tableIndex = i;
	}
	
	
	private String main_MakingOrderToString(ArrayList<Order> orders) {
		
		String s = "";
		
		for (int i = 0; i<orders.size(); i++) {
			s += orders.get(i).menuName + "&nbsp;&nbsp;&nbsp;&nbsp;" + orders.get(i).orderCount + "<br>";
		}
		if (s == "") {
			return "주문 없음";
		}
		
		return s;
	}
	
	private JPanel main_MakingTableBtn(ArrayList<Table> tables, JPanel panel, CardLayout cardLayout, JPanel cardPanel) {
		
		panel = new JPanel();
		
		Table table;
		String tableString;
		
		for (int i = 0; i < tables.size(); i++ ) {
			//버튼에 들어갈 메뉴와 텍스트
			table = tables.get(i);
			tableString = "<HTML><b>" + table.getTableName() + "</b><br><br><br>" + main_MakingOrderToString(table.getOrders()) + "<br><br>" + table.getTotal() + "원</HTML>";
			JButton tempBtn = new JButton(tableString); //버튼 생성	
			if (!tables.get(i).getAvailable())
				tempBtn.setBackground(Color.lightGray);
			else
				tempBtn.setBackground(Color.cyan);
			tempBtn.setPreferredSize(new Dimension(170, 200));
			tempBtn.setHorizontalAlignment(SwingConstants.LEFT);
			tempBtn.setFont(new Font("Gulim", Font.PLAIN, 16));
			tempBtn.addActionListener(new ActionListener() { //버튼 누를때 호출
				public void actionPerformed(ActionEvent e) {
					//tableString 파싱
					String[] buttonName = e.getActionCommand().split("</b>");
					buttonName = buttonName[0].split("<HTML><b>");
					//인덱스
					int index = bab.searchTable(new Table(buttonName[1]));
					setTableIndex(index);					
					//총 주문 목록 보기 로 연결
					cardLayout.show(cardPanel, "TableInfo");
					button_Invisible();
				}
			});
		
		panel.add(tempBtn);
		panel.setLayout(new GridLayout(0, 3, 0, 0));
		}
		return panel;
	}
	
	private void order_BtnSaveIndex(int index, int amo) {
		
		int searchedIndex = -1; //orderTemp에 위치한 인텍스 찾는 변수
		if (orderTemp.size() != 0) {
			for (int i = 0; i < orderTemp.size(); i+=2) {
				if (index == orderTemp.get(i)) {
					searchedIndex = i;
					break;
				}
			}
		}
		
		if (searchedIndex != -1) {
			Menu menu = bab.menus.get(index);
			orderModel.removeRow((searchedIndex+1)/2);
			orderModel.insertRow((searchedIndex+1)/2, new Object[]{menu.getMenuName(), menu.getPrice(), amo + orderTemp.get(searchedIndex+1), menu.getPrice() * (amo + orderTemp.get(searchedIndex+1))});
			orderTemp.add(searchedIndex+1, orderTemp.get(searchedIndex+1)+amo);
			orderTemp.remove(searchedIndex+2);
		}
		else {
			Menu menu = bab.menus.get(index);
			orderModel.insertRow(orderModel.getRowCount(), new Object[]{menu.getMenuName(), menu.getPrice(), amo, menu.getPrice()*amo});
			
			orderTemp.add(index);
			orderTemp.add(amo);
			
		}
		
	}
	
	private void order_MakingMenuBtn(ArrayList<Menu> menus, JPanel panel) {
		
		Menu menu;
		String menuString;
		
		for (int i = 0; i < menus.size(); i++ ) {
			//버튼에 들어갈 메뉴와 텍스트
			menu = menus.get(i);
			menuString = "<HTML><b>" + menu.getMenuName() + "</b><br><br><br>" + menu.getPrice() + "</HTML>";
			JButton tempBtn = new JButton(menuString); //버튼 생성
			tempBtn.setPreferredSize(new Dimension(100, 100));
			tempBtn.setHorizontalAlignment(SwingConstants.LEFT);
			tempBtn.addActionListener(new ActionListener() { //버튼 누를때 호출
				public void actionPerformed(ActionEvent e) {
					//menuString 파싱
					String[] buttonName = e.getActionCommand().split("</b>");
					buttonName = buttonName[0].split("<HTML><b>");
					int index = bab.searchMenu(new Menu(buttonName[1])); //인덱스
					int amo = Integer.valueOf(JOptionPane.showInputDialog(null, "수량을 입력해주세요.", JOptionPane.INFORMATION_MESSAGE));
					if (amo <= 0) {
						JOptionPane.showMessageDialog(null, "0 또는 음수는 입력할 수 없습니다." , "주문하기", JOptionPane.WARNING_MESSAGE);
						return;
					}
					order_BtnSaveIndex(index, amo);
				}
			});
		
		panel.add(tempBtn);
		}
	}
	
	private void orderOnUI() { //오더 출력용
		//표 정보 변경
		for (int i = 0; i < bab.uiUseOrder(tableIndex).length; i++)
			orderModel.insertRow(i, bab.uiUseOrder(tableIndex)[i]);
	}
	
	
	private String[] comboboxMaker(ArrayList<Table> tables) {
		
		String[] list = new String[tables.size()+1];
		list[0] = "테이블을 선택하세요.";
		
		for (int i = 1; i < tables.size()+1; i++) {
			list[i] = tables.get(i-1).getTableName();
		}
		
		return list;
	}
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new UI();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UI() {
		initialize();
		frame.setVisible(true);
	}


	private void initialize() {
			
			try {			
				//시작하면 기존에 저장된 설정을 불러온다. 존재하지 않는 파일에 대한 경우 함수 내부에서 exception 발생
				bab = bab.load(bab, loader, dtLoader, obLoader);
			} 
			
			catch (FileNotFoundException ne) {
				JOptionPane.showMessageDialog(null, "환영합니다! 처음 사용하시는군요. \n관리자 모드로 들어가 테이블과 메뉴를 먼저 입력하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
				bab = new Restaurant(); //초기화
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "파일 읽기 중 문제가 발생했습니다. 프로그램을 종료합니다.", "오류", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			frame = new JFrame();
			frame.setResizable(false);
			frame.setBounds(100, 100, 937, 657);
			frame.setSize(937, 657);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(null);
			
			//카드레이아웃 선언
			JPanel cardPanel = new JPanel();
			cardPanel.setBounds(123, 93, 642, 490);
			frame.getContentPane().add(cardPanel);
			CardLayout cardLayout = new CardLayout(0, 0);
			cardPanel.setLayout(cardLayout);
			
			
			
			//관리자 버튼
			btnAdmin.setBounds(23, 445, 88, 138);
			btnAdmin.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int i = JOptionPane.showConfirmDialog(null, "관리자 모드로 이동합니다. 저장하지 않은 데이터는 모두 삭제됩니다. \n계속 하시겠습니까?", "관리자 모드로 전환", JOptionPane.YES_NO_OPTION ,JOptionPane.WARNING_MESSAGE);
					if (i == 0) {
						new UI2();
						frame.setVisible(false);
					}
				}
			});
			frame.getContentPane().add(btnAdmin);
			
			
			
			//체크아웃
			btnQuick_checkout.setBounds(777, 163, 131, 69);
			btnQuick_checkout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cardLayout.show(cardPanel, "CheckOut");
					button_Invisible();
				}
			});
			frame.getContentPane().add(btnQuick_checkout);
			
			//매출조회
			btnQuick_amount.setEnabled(true);
			btnQuick_amount.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(null, "현재 매출 : " + bab.getAmount() + "원" , "매출 조회", JOptionPane.INFORMATION_MESSAGE);
				}
			});
			btnQuick_amount.setBounds(777, 448, 131, 38);
			frame.getContentPane().add(btnQuick_amount);
			
			//저장
			btnQuick_save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						bab.save(writer, dtWriter, obWriter);
						JOptionPane.showMessageDialog(null, "저장을 완료했습니다." , "현재 상태 저장", JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "저장을 실패했습니다." , "현재 상태 저장", JOptionPane.ERROR_MESSAGE);
					}
					
				}
			});
			btnQuick_save.setBounds(777, 496, 131, 38);
			frame.getContentPane().add(btnQuick_save);
			
			//종료
			btnQuick_off.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int i = JOptionPane.showConfirmDialog(null, "정말로 종료하시나요?", "알림", JOptionPane.YES_NO_OPTION ,JOptionPane.INFORMATION_MESSAGE);
					if (i == 0)
						System.exit(0);
				}
			});
			btnQuick_off.setBounds(777, 545, 131, 38);
			frame.getContentPane().add(btnQuick_off);
			
			JLabel lblNewLabel_1 = new JLabel("식당 테이블 관리 프로그램");
			lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
			lblNewLabel_1.setBounds(123, 10, 642, 74);
			frame.getContentPane().add(lblNewLabel_1);
			
			

			//메뉴 주문 버튼
			btnQuick_order.setEnabled(true);
			btnQuick_order.setBounds(777, 84, 131, 69);
			btnQuick_order.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cardLayout.show(cardPanel, "AddOrder");
					setTableIndex(-1);
					orderTemp = new ArrayList<Integer>();
					button_Invisible();
				}
			});
			frame.getContentPane().add(btnQuick_order);
			
			JPanel panelMain_1 = new JPanel();
			cardPanel.add(panelMain_1, "Main");
			panelMain_1.setLayout(null);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(0, 0, 642, 490);
			panelMain_1.add(scrollPane);
			JPanel panel = new JPanel();
			scrollPane.setViewportView(main_MakingTableBtn(bab.getTables(), panel, cardLayout, cardPanel));
			
			//메인화면 테이블 버튼 추가
			panelMain_1.addComponentListener(new ComponentListener() {
				public void componentShown(ComponentEvent e) {
					//메인화면 테이블 버튼 추가
					scrollPane.setViewportView(main_MakingTableBtn(bab.getTables(), panel, cardLayout, cardPanel));
				}
				public void componentResized(ComponentEvent e) {}
				public void componentMoved(ComponentEvent e) {}
				public void componentHidden(ComponentEvent e) {}
			});
			
			
			
			
			
			JPanel panelAddOrder = new JPanel();
			panelAddOrder.setLayout(null);
			cardPanel.add(panelAddOrder, "AddOrder");
			
			JComboBox<String> comboBoxAddOrder = new JComboBox<String>(comboboxMaker(bab.getTables()));
			comboBoxAddOrder.setBounds(12, 35, 259, 38);
			panelAddOrder.add(comboBoxAddOrder);
			
			JPanel panel_2 = new JPanel();
			panel_2.setBounds(283, 10, 345, 470);
			panelAddOrder.add(panel_2);
			panel_2.setLayout(null);
			
			JScrollPane scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(0, 0, 345, 468);
			panel_2.add(scrollPane_1);
			
			JPanel panelAddOrder_menuPanel = new JPanel();
			panelAddOrder_menuPanel.setBounds(0, 0, 345, 468);
			scrollPane_1.setViewportView(panelAddOrder_menuPanel);
			
			//메뉴들의 버튼 생성
			order_MakingMenuBtn(bab.menus, panelAddOrder_menuPanel);
			panelAddOrder_menuPanel.setLayout(new GridLayout(0, 3, 1, 1));
			
			//주문하기 ------------------------------------------------------------
			JButton orderpage_btnOrder = new JButton("주문하기");
			orderpage_btnOrder.setBounds(12, 360, 259, 53);
			orderpage_btnOrder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if ((String)comboBoxAddOrder.getSelectedItem() == "테이블을 선택하세요."){ //작업중인 내역이 없다면 그냥 돌려보냄
						JOptionPane.showMessageDialog(null, "테이블을 선택하세요." , "주문하기", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (orderTemp.size() == 0) {
						int i = JOptionPane.showConfirmDialog(null, "<html>주문 내역이 존재하지 않습니다.<br>해당 테이블에 대해 예약을 진행합니까?</html>", "예약하기", JOptionPane.YES_NO_OPTION ,JOptionPane.INFORMATION_MESSAGE);
						if (i == 0) {
							//주문 함수
							tableIndex = bab.searchTable(new Table((String)comboBoxAddOrder.getSelectedItem()));
							if (!bab.tables.get(tableIndex).getAvailable()) {
								JOptionPane.showMessageDialog(null, "<html>이미 예약이 되어 있거나 사용중인 좌석입니다.<br>예약 취소는 계산을 통해 진행해주세요.</html>" , "예약하기", JOptionPane.WARNING_MESSAGE);
								return;
							}
							bab.tables.get(tableIndex).inTable();
							JOptionPane.showMessageDialog(null, "<html>예약이 완료되었습니다.<br>예약 취소는 계산을 통해 진행해주세요.</html>" , "예약하기", JOptionPane.INFORMATION_MESSAGE);
							orderModel.setRowCount(0);
							cardLayout.show(cardPanel, "Main");
							comboBoxAddOrder.setSelectedIndex(0);
							button_Visible();
							return;
							
						}
					}
					int i = JOptionPane.showConfirmDialog(null, "지금까지의 내역을 주문하시겠습니까?", "주문하기", JOptionPane.YES_NO_OPTION ,JOptionPane.WARNING_MESSAGE);
					if (i == 0) {
						//주문 함수
						tableIndex = bab.searchTable(new Table((String)comboBoxAddOrder.getSelectedItem()));
						bab.tables.get(tableIndex).inTable();
						for (int j = 0; j < orderTemp.size(); j+=2) {
							bab.tables.get(tableIndex).addOrder(new Order(bab.menus.get(orderTemp.get(j))), orderTemp.get(j+1));
						}
						JOptionPane.showMessageDialog(null, "주문이 완료되었습니다." , "주문하기", JOptionPane.INFORMATION_MESSAGE);
						orderModel.setRowCount(0);
						cardLayout.show(cardPanel, "Main");
						comboBoxAddOrder.setSelectedIndex(0);
						button_Visible();
						
					}
				}
			});
			panelAddOrder.add(orderpage_btnOrder);
			
			JButton orderpage_btnBack = new JButton("뒤로가기");
			orderpage_btnBack.setBounds(12, 423, 259, 53);
			orderpage_btnBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if ((String)comboBoxAddOrder.getSelectedItem() == "테이블을 선택하세요."){ //작업중인 내역이 없다면 그냥 돌려보냄
						cardLayout.show(cardPanel, "Main");
						orderModel.setRowCount(0);
						comboBoxAddOrder.setSelectedIndex(0);
						button_Visible();
					}
					else {
						int i = JOptionPane.showConfirmDialog(null, "주문을 취소하고 뒤로 갑니다.\n계속 하시겠습니까?", "뒤로가기", JOptionPane.YES_NO_OPTION ,JOptionPane.WARNING_MESSAGE);
						if (i == 0) {
							cardLayout.show(cardPanel, "Main");
							comboBoxAddOrder.setSelectedIndex(0);
							button_Visible();
						}
					}
				}
			});
			panelAddOrder.add(orderpage_btnBack);
			
			JLabel lblNewLabel_3 = new JLabel("주문하는 테이블 이름");
			lblNewLabel_3.setBounds(12, 10, 259, 15);
			panelAddOrder.add(lblNewLabel_3);
			
			JLabel lblNewLabel_2_1 = new JLabel("주문 내역");
			lblNewLabel_2_1.setBounds(12, 111, 123, 15);
			panelAddOrder.add(lblNewLabel_2_1);
			
			//테이블 삽입될 스크롤판-----------------------------
			JTable tableOnCheckIn = new JTable(orderModel);
			JScrollPane orderpage_scrollPane = new JScrollPane(tableOnCheckIn);
			orderpage_scrollPane.setBounds(12, 136, 259, 214);
			panelAddOrder.add(orderpage_scrollPane);
			
			//체크아웃 패널
			JPanel panelCheckOut = new JPanel();
			panelCheckOut.setLayout(null);
			cardPanel.add(panelCheckOut, "CheckOut");
			
			JTextArea checkout_priceText = new JTextArea();
			checkout_priceText.setFont(new Font("Monospaced", Font.PLAIN, 16));
			checkout_priceText.setEditable(false);
			checkout_priceText.setText("0");
			checkout_priceText.setBounds(24, 212, 214, 60);
			panelCheckOut.add(checkout_priceText);
			
			//테이블 선택 체크아웃용 콤보박스
			JComboBox<String> checkout_comboBox = new JComboBox<String>(comboboxMaker(bab.getTables()));
			checkout_comboBox.setBounds(24, 55, 247, 60);
			checkout_comboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					orderModel.setRowCount(0);
					try {
						setTableIndex(bab.searchTable(new Table((String)checkout_comboBox.getSelectedItem())));
						checkout_priceText.setText(Integer.toString(bab.tables.get(tableIndex).getTotal()));
					}
					catch (java.lang.IndexOutOfBoundsException ex) {} //검색 결과가 없는 경우인데 넘긴다
					orderOnUI();
				}
			});
			panelCheckOut.add(checkout_comboBox);
			
			//테이블 삽입될 스크롤판--------------------------------------
			JTable tableOnCheckOut = new JTable(orderModel);
			JScrollPane checkout_scrollPane = new JScrollPane(tableOnCheckOut);
			checkout_scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			checkout_scrollPane.setBounds(309, 53, 305, 422);
			panelCheckOut.add(checkout_scrollPane);
			
			JButton checkout_btnCheckout = new JButton("계산하기");
			checkout_btnCheckout.setBounds(24, 358, 259, 53);
			checkout_btnCheckout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if ((String)checkout_comboBox.getSelectedItem() == "테이블을 선택하세요."){ //작업중인 내역이 없다면 그냥 돌려보냄
						JOptionPane.showMessageDialog(null, "테이블을 선택하세요." , "주문하기", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(bab.tables.get(tableIndex).getOrders().size() == 0) {
						int i = JOptionPane.showConfirmDialog(null, "정말 예약을 취소 하시겠습니까?", "예약 취소하기", JOptionPane.YES_NO_OPTION ,JOptionPane.WARNING_MESSAGE);
						if (i == 0) {
							bab.tables.get(tableIndex).outTable(); //계산
							JOptionPane.showMessageDialog(null, "예약취소가 완료되었습니다." , "예약 취소하기", JOptionPane.INFORMATION_MESSAGE);
							cardLayout.show(cardPanel, "Main");
							button_Visible();
							checkout_priceText.setText("0");
							checkout_comboBox.setSelectedIndex(0); //익셉션 발생하지만 문제없음
							return;
						}
					}
						
					int i = JOptionPane.showConfirmDialog(null, "정말 계산하시겠습니까?", "계산하기", JOptionPane.YES_NO_OPTION ,JOptionPane.WARNING_MESSAGE);
					if (i == 0) {
						orderModel.setRowCount(0); //먼저 객체 삭제
						bab.pay(bab.tables.get(tableIndex)); //계산
						JOptionPane.showMessageDialog(null, "계산이 완료되었습니다." , "계산하기", JOptionPane.INFORMATION_MESSAGE);
						cardLayout.show(cardPanel, "Main");
						button_Visible();
						checkout_priceText.setText("0");
						checkout_comboBox.setSelectedIndex(0); //익셉션 발생하지만 문제없음
					}
				}
			});
			panelCheckOut.add(checkout_btnCheckout);
			
			JButton checkout_btnBack = new JButton("뒤로가기");
			checkout_btnBack.setBounds(24, 422, 259, 53);
			checkout_btnBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if ((String)checkout_comboBox.getSelectedItem() == "테이블을 선택하세요."){ //작업중인 내역이 없다면 그냥 돌려보냄
						cardLayout.show(cardPanel, "Main");
						button_Visible();
						checkout_priceText.setText("0");
						checkout_comboBox.setSelectedIndex(0); //익셉션 발생하지만 문제 없음
					}
					else {
						int i = JOptionPane.showConfirmDialog(null, "계산을 취소하고 뒤로 갑니다.\n계속 하시겠습니까?", "뒤로가기", JOptionPane.YES_NO_OPTION ,JOptionPane.WARNING_MESSAGE);
						if (i == 0) {
							cardLayout.show(cardPanel, "Main");
							button_Visible();
							checkout_comboBox.setSelectedIndex(0); //익셉션 발생하지만 문제 없음
						}
					}
				}
			});
			panelCheckOut.add(checkout_btnBack);
			
			
			
			
			JLabel lblNewLabel_2 = new JLabel("계산하는 테이블 이름");
			lblNewLabel_2.setBounds(24, 30, 259, 15);
			panelCheckOut.add(lblNewLabel_2);
			
			JLabel lblNewLabel_4 = new JLabel("결제 금액");
			lblNewLabel_4.setBounds(24, 186, 247, 15);
			panelCheckOut.add(lblNewLabel_4);
			
			JLabel lblNewLabel_5 = new JLabel("주문 내역");
			lblNewLabel_5.setBounds(309, 30, 126, 15);
			panelCheckOut.add(lblNewLabel_5);
			
			JLabel lblNewLabel_6 = new JLabel("원");
			lblNewLabel_6.setFont(new Font("Gulim", Font.PLAIN, 17));
			lblNewLabel_6.setBounds(250, 239, 52, 35);
			panelCheckOut.add(lblNewLabel_6);
			
			JPanel panelTableInfo = new JPanel();
			panelTableInfo.setLayout(null);
			panelTableInfo.addComponentListener(new ComponentListener() {
				public void componentShown(ComponentEvent e) {
					//라벨 변경
					Table tableInfo = bab.tables.get(tableIndex);
					tableInfo_titleLabel.setText(tableInfo.getTableName()+"의 주문내역");
					//표 정보 변경
					orderOnUI();
				}
				public void componentResized(ComponentEvent e) {}
				public void componentMoved(ComponentEvent e) {}
				public void componentHidden(ComponentEvent e) {}
			});
			cardPanel.add(panelTableInfo, "TableInfo");
			
			
			//테이블 들어올 스크롤판---------------------------------------
			tableOnInfo = new JTable(orderModel);
			
			JScrollPane tableInfo_scrollPane = new JScrollPane(tableOnInfo);
			tableInfo_scrollPane.setBounds(12, 55, 616, 376);	
			panelTableInfo.add(tableInfo_scrollPane);
			
			
			JButton tableInfo_btnBack = new JButton("뒤로가기");
			tableInfo_btnBack.setBounds(248, 441, 148, 47);
			tableInfo_btnBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					orderModel.setRowCount(0);
					cardLayout.show(cardPanel, "Main");
					button_Visible();
				}
			});
			panelTableInfo.add(tableInfo_btnBack);

			tableInfo_titleLabel.setBounds(258, 22, 308, 23);
			panelTableInfo.add(tableInfo_titleLabel);
			
		}
	}
