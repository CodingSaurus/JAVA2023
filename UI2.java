import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import java.awt.Component;

public class UI2 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtMenuPrice;
	private JTextField txtMenuName;
	private JTextField txtMenuSearch;
	private JTextField txtTablePeople;
	private JTextField txtTableName;
	private JTextField txtTableSearch;
	
	//레스토랑 클래스
	Restaurant bab = new Restaurant();
	//파일 입출력
	FileOutputStream writer = null;
	DataOutputStream dtWriter = null;
	ObjectOutputStream obWriter = null;
	FileInputStream loader = null;
	DataInputStream dtLoader = null;
	ObjectInputStream obLoader = null;
	//JTable
	String colNames[] = {"*", "이름", "가격"};
	String colNames2[] = {"*", "이름", "인원"};
	Object data[][] = {}; //메뉴
	Object data2[][] = {}; //테이블
	//Object data2[][] = {{"1", "짜장면", "6000"},{"2", "간짜장", "6500"}, ...
	public DefaultTableModel menuModel;
	public DefaultTableModel tableModel;

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new UI2();
				
			}
		});
	}
	
	public void loadMenuAndTable(Restaurant bab) { //최초 실행 시 JTable에 표시할 수 있는 형태로 변환
		data = bab.uiUseMenu();
		data2 = bab.uiUseTable();		
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
	
	
	public UI2() {
		
		
		try {			
			//시작하면 기존에 저장된 설정을 불러온다. 존재하지 않는 파일에 대한 경우 함수 내부에서 exception 발생
			bab = bab.load(bab, loader, dtLoader, obLoader);
			loadMenuAndTable(bab);
			setVisible(true);
		} 
		
		catch (FileNotFoundException ne) {
			JOptionPane.showMessageDialog(null, "탭을 클릭해 메뉴와 테이블을 먼저 등록해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
			bab = new Restaurant(); //초기화
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "파일 읽기 중 문제가 발생했습니다. 프로그램을 종료합니다.", "오류", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 645);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("[관리자 모드] 식당 테이블 관리 프로그램");
		lblNewLabel.setBounds(325, 10, 324, 56);
		contentPane.add(lblNewLabel);
		
		JButton buttonSave = new JButton("완료 및 저장");
		buttonSave.setBounds(588, 567, 137, 31);
		buttonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					bab.save(writer, dtWriter, obWriter);
				} catch (IOException ioe) { //읽기 중 에러 발생시
					JOptionPane.showMessageDialog(null, "저장에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
					ioe.printStackTrace();
				}
				finally {
					closeOutputStream(writer, dtWriter, obWriter);
					JOptionPane.showMessageDialog(null, "저장을 완료했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		contentPane.add(buttonSave);
		
		JButton buttonUI1 = new JButton("매장 모드");
		buttonUI1.setBounds(737, 567, 137, 31);
		//매장모드 이동하는 기능
		buttonUI1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = JOptionPane.showConfirmDialog(null, "매장 모드로 이동합니다. 저장하지 않은 데이터는 모두 삭제됩니다. \n계속 하시겠습니까?", "매장 모드로 전환", JOptionPane.YES_NO_OPTION ,JOptionPane.WARNING_MESSAGE);
				if (i == 0) {
					new UI();
					setVisible(false);
				}
			}
		});
		contentPane.add(buttonUI1);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbedPane.setBounds(12, 76, 862, 504);
		contentPane.add(tabbedPane);
		
		
		//메인! (패널1)
		JPanel panelMain = new JPanel();
		panelMain.setLayout(null);
		tabbedPane.addTab("메인", null, panelMain, null);
		
		JLabel labelMainInfo = new JLabel("하단 버튼을 클릭해 관리하고자 하는 메뉴를 선택해주세요.");
		labelMainInfo.setBounds(12, 428, 392, 34);
		panelMain.add(labelMainInfo);
		
		//메뉴 관리 (패널2)
		JPanel panelMenu = new JPanel();
		panelMenu.setLayout(null);
		tabbedPane.addTab("메뉴 관리", null, panelMenu, null);
		
		txtMenuSearch = new JTextField();
		txtMenuSearch.setColumns(10);
		txtMenuSearch.setBounds(550, 21, 200, 20);
		panelMenu.add(txtMenuSearch);
		
		
		
		
		JLabel labelMenuPrice = new JLabel("가격");
		labelMenuPrice.setBounds(205, 384, 46, 15);
		panelMenu.add(labelMenuPrice);
		
		JLabel labelMenuName = new JLabel("이름");
		labelMenuName.setBounds(205, 353, 46, 15);
		panelMenu.add(labelMenuName);
		
		txtMenuPrice = new JTextField();
		txtMenuPrice.setColumns(10);
		txtMenuPrice.setBounds(263, 381, 106, 21);
		panelMenu.add(txtMenuPrice);
		
		txtMenuName = new JTextField();
		txtMenuName.setColumns(10);
		txtMenuName.setBounds(263, 350, 106, 21);
		panelMenu.add(txtMenuName);
		
		//테이블 추가제거를 위해 모델로 생성
		menuModel = new DefaultTableModel(data, colNames);
		
		JTable menu = new JTable(menuModel);
		menu.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				
				String search = txtMenuSearch.getText().trim();
				
				if(!search.isEmpty()) {
					JOptionPane.showMessageDialog(null, "검색 중에는 표 선택 기능을 사용할 수 없습니다. \n직접 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
					return;
				}
					
					
				txtMenuName.setText(menuModel.getValueAt(menu.getSelectedRow(), 1).toString());
				txtMenuPrice.setText(menuModel.getValueAt(menu.getSelectedRow(), 2).toString());
					
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		menu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //단일선택
		
		TableRowSorter<DefaultTableModel> menuSorter = new TableRowSorter<>(menuModel);
		menu.setRowSorter(menuSorter);
		
		JScrollPane scrollPaneMenu = new JScrollPane(menu);
		scrollPaneMenu.setBounds(36, 53, 790, 273);
		panelMenu.add(scrollPaneMenu);
		
		
		JButton buttonMenuSearch = new JButton("검색");
		buttonMenuSearch.setBounds(750, 21, 75, 20);
		buttonMenuSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					String search = txtMenuSearch.getText().trim();
					
					if (search.isEmpty()) {
						menuSorter.setRowFilter(null);
						JOptionPane.showMessageDialog(null, "모든 메뉴를 출력합니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					menuSorter.setRowFilter(RowFilter.regexFilter(search));
					JOptionPane.showMessageDialog(null, "\'"+txtMenuSearch.getText()+"\'"+"에 대한 검색 결과입니다.");
					
				}
					catch (NullPointerException ne) {
					JOptionPane.showMessageDialog(null, "메뉴 명을 입력해주세요.", "오류", JOptionPane.WARNING_MESSAGE);
				} catch (PatternSyntaxException e1) {
					JOptionPane.showMessageDialog(null, "잘 입력 했는지 다시 한 번 확인하세요.", "오류", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		panelMenu.add(buttonMenuSearch);
		
		//여기까지 JTable
		
		JLabel labelMenuAdd = new JLabel("메뉴 추가 및 제거");
		labelMenuAdd.setBounds(382, 21, 148, 15);
		panelMenu.add(labelMenuAdd);
		
		JButton buttonMenuAdd = new JButton("<HTML>메뉴 추가<br>/ 수정</HTML>");
		buttonMenuAdd.setBounds(494, 350, 95, 67);
		buttonMenuAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				
					int index = bab.searchMenu(new Menu(txtMenuName.getText(), Integer.parseInt(txtMenuPrice.getText())));
				
					if (index == -1) {
						bab.addMenu(new Menu(txtMenuName.getText(), Integer.parseInt(txtMenuPrice.getText())));
						menuModel.insertRow(menuModel.getRowCount(), new Object[]{menuModel.getRowCount()+1,txtMenuName.getText(),txtMenuPrice.getText()});
						JOptionPane.showMessageDialog(null, "\'"+txtMenuName.getText()+"\'"+"(이/가) 추가되었습니다.");
						menu.updateUI();
					} 
					
					else {
						//수정도 같이 해주는 addMenu
						bab.addMenu(new Menu(txtMenuName.getText(), Integer.parseInt(txtMenuPrice.getText())));
						menuModel.removeRow(index);
						menuModel.insertRow(index, new Object[]{index+1,txtMenuName.getText(),txtMenuPrice.getText()});
						JOptionPane.showMessageDialog(null, "\'"+txtMenuName.getText()+"\'"+"(이/가) 수정되었습니다.");
					}
				}
				catch (NullPointerException ne) {
					JOptionPane.showMessageDialog(null, "빼먹은 칸이 없는지 다시 한 번 확인하세요.", "오류", JOptionPane.WARNING_MESSAGE);
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "잘 입력 했는지 다시 한 번 확인하세요.", "오류", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panelMenu.add(buttonMenuAdd);
		
		JButton buttonMenuDel = new JButton("메뉴 삭제");
		buttonMenuDel.setBounds(616, 350, 95, 67);
		buttonMenuDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		
				try {
					//모델에서의 삭제용
					int index = bab.searchMenu(new Menu(txtMenuName.getText()));
					
					if (index == -1)
						JOptionPane.showMessageDialog(null, "존재하지 않는 메뉴입니다.");
					else {
						bab.deleteMenu(new Menu(txtMenuName.getText()));
						menuModel.removeRow(index);
						JOptionPane.showMessageDialog(null, "\'"+txtMenuName.getText()+"\'"+"(이/가) 삭제 되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				catch (NullPointerException ne) {
					JOptionPane.showMessageDialog(null, "메뉴 명을 입력해주세요.", "오류", JOptionPane.WARNING_MESSAGE);
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "잘 입력 했는지 다시 한 번 확인하세요.", "오류", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		panelMenu.add(buttonMenuDel);
		
		JLabel labelMenuDel = new JLabel("메뉴 삭제 시 가격 미입력");
		labelMenuDel.setBounds(263, 412, 148, 15);
		panelMenu.add(labelMenuDel);
		
		
		
		//테이블 관리
		JPanel panelTable = new JPanel();
		panelTable.setLayout(null);
		tabbedPane.addTab("테이블 관리", null, panelTable, null);
		
				
		JLabel labelTablePeople = new JLabel("인원");
		labelTablePeople.setBounds(206, 384, 45, 15);
		panelTable.add(labelTablePeople);
		
		JLabel labelTableName = new JLabel("이름");
		labelTableName.setBounds(206, 353, 45, 15);
		panelTable.add(labelTableName);
		
		txtTablePeople = new JTextField();
		txtTablePeople.setColumns(10);
		txtTablePeople.setBounds(263, 381, 106, 21);
		panelTable.add(txtTablePeople);
		
		txtTableName = new JTextField();
		txtTableName.setColumns(10);
		txtTableName.setBounds(263, 350, 106, 21);
		panelTable.add(txtTableName);
		
		
		
		
		//여기부터 JTable(윈도우빌더 사용시 주석처리)
		
		//테이블 추가제거를 위해 모델로 생성
		tableModel = new DefaultTableModel(data2, colNames2);
		
		JTable table = new JTable(tableModel);
		table.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			
				String search = txtTableSearch.getText().trim();
				
				if(!search.isEmpty()) {
					JOptionPane.showMessageDialog(null, "검색 중에는 표 선택 기능을 사용할 수 없습니다. \n직접 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				//선택한 셀의 행 번호계산 
				int row = table.getSelectedRow();
							  
				//선택한 테이블의 row 값을 이용해서 데이터 삽입
				txtTableName.setText(String.valueOf(tableModel.getValueAt(row, 1)));
				txtTablePeople.setText(String.valueOf(tableModel.getValueAt(row, 2)));

							
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //단일선택
				
		JScrollPane scrollPaneTable = new JScrollPane(table);
		scrollPaneTable.setBounds(36, 53, 790, 273);
		panelTable.add(scrollPaneTable);
		
		txtTableSearch = new JTextField();
		txtTableSearch.setColumns(10);
		txtTableSearch.setBounds(550, 21, 200, 20);
		panelTable.add(txtTableSearch);
				
				
		JLabel labelTableAdd = new JLabel("테이블 추가 및 제거");
		labelTableAdd.setBounds(382, 21, 148, 15);
		panelTable.add(labelTableAdd);
		
		JButton buttonTableAdd = new JButton("<HTML>테이블 추가<br> / 수정</HTML>");
		buttonTableAdd.setBounds(494, 350, 95, 67);
		buttonTableAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				
					int index = bab.searchTable(new Table(txtTableName.getText()));
				
					if (index == -1) {
						bab.addTable(new Table(txtTableName.getText(), Integer.parseInt(txtTablePeople.getText()), true));
						tableModel.insertRow(tableModel.getRowCount(), new Object[]{tableModel.getRowCount()+1,txtTableName.getText(),txtTablePeople.getText()});
						JOptionPane.showMessageDialog(null, "\'"+txtTableName.getText()+"\'"+"(이/가) 추가되었습니다.");
						menu.updateUI();
					} 
					
					else {
						//수정도 같이 해주는 addTable
						bab.addTable(new Table(txtTableName.getText(), Integer.parseInt(txtTablePeople.getText()), true));
						tableModel.removeRow(index);
						tableModel.insertRow(index, new Object[]{index+1,txtTableName.getText(),txtTablePeople.getText()});
						JOptionPane.showMessageDialog(null, "\'"+txtTableName.getText()+"\'"+"(이/가) 수정되었습니다.");
					}
				}
				catch (NullPointerException ne) {
					JOptionPane.showMessageDialog(null, "빼먹은 칸이 없는지 다시 한 번 확인하세요.", "오류", JOptionPane.WARNING_MESSAGE);
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "잘 입력 했는지 다시 한 번 확인하세요.", "오류", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panelTable.add(buttonTableAdd);
		
		
		JButton buttonTableDel = new JButton("<HTML>테이블<br>삭제</HTML>");
		buttonTableDel.setBounds(616, 350, 95, 67);
		buttonTableDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		
				try {
					//모델에서의 삭제용
					int index = bab.searchTable(new Table(txtTableName.getText()));
					
					if (index == -1)
						JOptionPane.showMessageDialog(null, "존재하지 않는 테이블입니다.");
					else {
						bab.deleteTable(new Table(txtTableName.getText()));
						tableModel.removeRow(index);
						JOptionPane.showMessageDialog(null, "\'"+txtTableName.getText()+"\'"+"(이/가) 삭제 되었습니다.");
					}
				}
				catch (NullPointerException ne) {
					JOptionPane.showMessageDialog(null, "테이블 명을 입력해주세요.", "오류", JOptionPane.WARNING_MESSAGE);
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "잘 입력 했는지 다시 한 번 확인하세요.", "오류", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		panelTable.add(buttonTableDel);
		
		JLabel labelTableDel = new JLabel("테이블 삭제 시 인원 미입력");
		labelTableDel.setBounds(263, 412, 148, 15);
		panelTable.add(labelTableDel);
		
		TableRowSorter<DefaultTableModel> tableSorter = new TableRowSorter<>(tableModel);
		table.setRowSorter(tableSorter);
		
		
		JButton buttonTableSearch = new JButton("검색");
		buttonTableSearch.setBounds(750, 21, 75, 20);
		buttonTableSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					String search = txtTableSearch.getText().trim();
					
					if (search.isEmpty()) {
						tableSorter.setRowFilter(null);
						JOptionPane.showMessageDialog(null, "모든 테이블을 출력합니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					tableSorter.setRowFilter(RowFilter.regexFilter(search));
					JOptionPane.showMessageDialog(null, "\'"+txtTableSearch.getText()+"\'"+"에 대한 검색 결과입니다.");
					
				}
					catch (NullPointerException ne) {
					JOptionPane.showMessageDialog(null, "테이블 명을 입력해주세요.", "오류", JOptionPane.WARNING_MESSAGE);
				} catch (PatternSyntaxException e1) {
					JOptionPane.showMessageDialog(null, "잘 입력 했는지 다시 한 번 확인하세요.", "오류", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		panelTable.add(buttonTableSearch);
			
		
		
	}
	
	
}
