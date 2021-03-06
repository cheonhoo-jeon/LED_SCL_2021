package rfcommunication;

import gnu.io.*;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.swing.JTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;















import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RefineryUtilities;

import java.io.*;

public class guimake4  {

	
	public int st;

	public int numbyte;
	public String str;
	public byte[] readBuffers;

	public String Filepath="D:\\Dropbox\\0_JAVA\\txt_file";
	public File file = new File("D:\\Dropbox\\0_JAVA\\txt_file");
		
	private int datay;
	private JFrame frame;

	private JComboBox<String> paritysel = new JComboBox<String>();
	private JComboBox<String> stopsel = new JComboBox<String>();
	private JComboBox<String> databitsel = new JComboBox<String>();
	private JComboBox<String> portsel = new JComboBox<String>();
	private JComboBox<String> speedsel = new JComboBox<String>();
	public JList<String> monitoring2 = new JList<String>();
	public JScrollPane scrollPane = new JScrollPane();
	public JScrollPane scrollPane2 = new JScrollPane();
	private JButton btnRefresh = new JButton("Refresh");
	private JButton btnReceiveData = new JButton("Receive Data");
	private JButton Connectbtn = new JButton("Connect");
	
	private CommPortIdentifier	commPortIdentifier	=null;
	private SerialPort			serialPort			=null;
	 
	private OutputStream	serialOut;
	private InputStream		serialIn;

	private DefaultListModel<String> model = new DefaultListModel<String>();
	private DefaultListModel<String> model2 = new DefaultListModel<String>();
	private JTextField sendtext;
	private FileOutputStream output;
	private FileOutputStream output2;

	//private data data= new data();
	private datam datam= new datam();

    private boolean findpattern = false;
    private int findready = 0;
    private int datanum=0;
    private JTextField samplerate;
    private double data_before=0;

	public int num_monitor =0;
	public int num_pre =0;
	public String str_monitor ="";
	public Date today = new Date (); 
    TimeSeries series = new TimeSeries("Random Data", Millisecond.class);
    private JTextField sampletime;

    long dataf=0;
    long data_repattern2=0;
	int wstart=0;
	String printdata="";
    //public graphs graph = new graphs("");
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					guimake4 window = new guimake4();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public guimake4() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 860, 434);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		makecombobox();
		
		
		// Connect Button
		// Connect to RS232 port
		Connectbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					connect();	// ????????????
					datam=new datam();   // data process class
					datam.samplenum=Integer.parseInt(samplerate.getText());    // setting sampling rate
					datam.pattern_plus=Integer.parseInt(sampletime.getText());    // setting pattern plus
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});		
		Connectbtn.setBounds(12, 40, 86, 21);
		frame.getContentPane().add(Connectbtn);
		
		// Refresh Button
		// Refresh All RS232 Connection
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (Connectbtn.getText().equals("Connect")) {
					appendPortList();
				}
				else if (Connectbtn.getText().equals("Disconnect")) {
					serialPort.close();
					serialPort = null;  
					Connectbtn.setText("Connect");
					appendPortList();
					try {
						connect();	// ????????????
						datam=new datam();   // data process class
						datam.samplenum=Integer.parseInt(samplerate.getText());    // setting sampling rate
						datam.pattern_plus=Integer.parseInt(sampletime.getText());    // setting pattern plus
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnRefresh.setBounds(114, 40, 86, 21);
		frame.getContentPane().add(btnRefresh);
		
		
		// data save file
		sendtext = new JTextField();
		sendtext.setText("D:\\\\Dropbox\\\\0_JAVA\\\\rx.txt");
		sendtext.setBounds(12, 71, 502, 21);
		frame.getContentPane().add(sendtext);
		sendtext.setColumns(10);
		
	
		
		
		// ReceiveData Button
		btnReceiveData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnReceiveData.getText()=="Receive Data"){

					btnReceiveData.setText("Stop Receive Data");
				} else if (btnReceiveData.getText()=="Stop Receive Data"){

					btnReceiveData.setText("Receive Data");
				}
	            
			}
		});
		btnReceiveData.setBounds(212, 40, 113, 21);
		frame.getContentPane().add(btnReceiveData);
		
		samplerate = new JTextField();
		samplerate.setBounds(337, 40, 82, 21);
		frame.getContentPane().add(samplerate);
		samplerate.setColumns(10);
		samplerate.setText("35");
		

        // sampling rate
        sampletime = new JTextField();
        sampletime.setText("10");
        sampletime.setBounds(447, 40, 67, 21);
        frame.getContentPane().add(sampletime);
        sampletime.setColumns(10);
        
		
		JPanel graph_pannel = new JPanel();
		graph_pannel.setBounds(12, 102, 502, 280);
		frame.getContentPane().add(graph_pannel);
		
		
        final TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        final JFreeChart chart = CreateChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        graph_pannel.add(chartPanel);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        
        

		scrollPane2 = new JScrollPane();
		scrollPane2.setBounds(530, 102, 302, 280);
		frame.getContentPane().add(scrollPane2);
		scrollPane2.setViewportView(monitoring2);
		monitoring2.setModel(model2);
	}

    public void adddata(String num){
    	final double factor = Double.valueOf(num);
        final Millisecond now = new Millisecond();
        //System.out.println(num);
        series.add(new Millisecond(), factor);
        data_before=factor;
    }
    
    private JFreeChart CreateChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            "Sensor Monitoring", 
            "Time", 
            "Code",
            dataset, 
            true, 
            true, 
            false
        );
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(32768.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 32768.0); 
        return result;
    }
    
	// ???????????? ??????????????? ?????? ????????????????????????.
	private void makecombobox(){

		// COM Port ComboBox
		portsel.setBounds(12, 9, 73, 21);
		frame.getContentPane().add(portsel);
		appendPortList();
		
		
		// Speed ComboBox		
		speedsel.setBounds(97, 9, 73, 21);
		frame.getContentPane().add(speedsel);
		speedsel.addItem("1200");
		speedsel.addItem("2400");
		speedsel.addItem("4800");
		speedsel.addItem("9600");
		speedsel.addItem("19200");
		speedsel.addItem("38400");
		speedsel.addItem("57600");
		speedsel.addItem("115200");
		speedsel.addItem("230400");
		speedsel.addItem("380400");
		speedsel.setSelectedIndex(8);


		// Parity ComboBox
		paritysel.setBounds(182, 9, 73, 21);
		frame.getContentPane().add(paritysel);
		paritysel.addItem("NONE");
		paritysel.addItem("ODD");
		paritysel.addItem("EVEN");
		paritysel.setSelectedIndex(0);

		// STOP ComboBox
		stopsel.setBounds(267, 9, 73, 21);
		frame.getContentPane().add(stopsel);
		stopsel.addItem("1 STOP");
		stopsel.addItem("2 STOP");
		stopsel.setSelectedIndex(0);
		
		// Databit ComboBox
		databitsel.setBounds(352, 9, 67, 21);
		frame.getContentPane().add(databitsel);
		databitsel.addItem("7 BIT");
		databitsel.addItem("8 BIT");
		databitsel.setSelectedIndex(1);

	}
    public void appendPortList(){  
  
        Enumeration<?> enumeration = null;  
  
        // ????????? ??????????????? ????????? ??????????????????.   
        portsel.removeAllItems();
          
        // ???????????? ???????????? ??????????????? ??????????????.  
        enumeration = CommPortIdentifier.getPortIdentifiers();  
        while ( enumeration.hasMoreElements() )   
        {  
            // ???????????? ?????????????????? ??????????????? ?????????????????? ?????????????????????.  
            commPortIdentifier = (CommPortIdentifier) enumeration.nextElement();  
            // ?????????????????? ?????????????? ???????????? ?????????????????? ????????????????????  ????????? ??????????????????.  
            if( commPortIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL )  
                portsel.addItem(commPortIdentifier.getName());  
        }  
       
        if( portsel.getItemCount() <= 0 ){  
            portsel.addItem("NO PORT");  
        }  
        else{  
            portsel.setSelectedIndex(portsel.getItemCount()-1);  
        }  
    }  
    
    public void connect() throws IOException{

		if (Connectbtn.getText().equals("Connect")) {

			Filepath=sendtext.getText();	// ???????????? ????????????????????? ????????????????????????
			String Filepath2 = "D:\\\\\\\\Dropbox\\\\\\\\0_JAVA\\\\\\\\rx.txt";	// ???????????? ????????????????????? ????????????????????????
			
			// Port ????????????????????????
			try {  
	            commPortIdentifier = CommPortIdentifier.getPortIdentifier(portsel.getItemAt(portsel.getSelectedIndex()));  
	        } catch (NoSuchPortException e1) {  
	              
	            e1.printStackTrace();  
	            return;  
	        }  
			
			// Port ????????????
	        try {  
	            serialPort = (SerialPort) commPortIdentifier.open(  
	                                "SerialTransferReceiver", 2000); 
	            //monitoring.clearSelection();
	            if (file.exists()==false)
	            {
	            	file.createNewFile();
	            }

	        	output = new FileOutputStream(Filepath);
	        	output2 = new FileOutputStream(Filepath2);
	        	wstart=0;
	            	
	        } catch (PortInUseException e1) {  
	            e1.printStackTrace();  
	            serialPort = null;  
	            return;  
	        }
	        
	        // RS-232 Parameter ????????????
	        // ????????????????????? ????????????   
	        try {  
	            int baud;  
	            int parity;  
	            int stop;  
	            int data;  
	              
	            baud = Integer.parseInt(speedsel.getItemAt(speedsel.getSelectedIndex()));  
	            switch( paritysel.getSelectedIndex()){  
	            case 0 : parity = SerialPort.PARITY_NONE;   break;  
	            case 1 : parity = SerialPort.PARITY_ODD;    break;  
	            case 2 : parity = SerialPort.PARITY_EVEN;   break;  
	            default: parity = SerialPort.PARITY_NONE;   break;    
	            }  
	            switch( stopsel.getSelectedIndex()){  
	            case 0 : stop = SerialPort.STOPBITS_1;  break;  
	            case 1 : stop = SerialPort.STOPBITS_2;  break;  
	            default: stop = SerialPort.STOPBITS_1;  break;    
	            }  
	            switch( databitsel.getSelectedIndex()){  
	            case 0 : data = SerialPort.DATABITS_7;  break;  
	            case 1 : data = SerialPort.DATABITS_8;  break;  
	            default: data = SerialPort.DATABITS_7;  break;    
	            }
	            serialPort.setSerialPortParams( baud, data, stop, parity);  
	        }   
	        catch (UnsupportedCommOperationException e1) {  
	            e1.printStackTrace();   
	        }
	        
	        
	        // ????????? ?????????????????? ????????????  
	        try {  
	            serialOut = serialPort.getOutputStream();  
	        } catch (IOException e1) {  
	            e1.printStackTrace();  
	        }  
	          
	        // ???????????? ?????????????????? ????????????  
	        try {  
	            serialIn = serialPort.getInputStream();  
	        } catch (IOException e1) {  
	            e1.printStackTrace();  
	        }  

	        // ???????????? ??????????????? ????????? ????????????   
	        serialPort.notifyOnDataAvailable(true);  
			try {  
			    serialPort.addEventListener( new SerialPortEventListener(){  
			        

					public void serialEvent(SerialPortEvent event ) {  
			            if( event.getEventType() == SerialPortEvent.DATA_AVAILABLE ){  
			              byte[] readBuffer = new byte[1];  							// ????????? ?????????????????? ?????????????????? ?????????

			                try {  
			                	
			                	
			                    while (serialIn.available() > 0) {  					// ????????? ?????????????????? ?????????????????? ????????????

			                    	numbyte = serialIn.read(readBuffer);				// 1byte ?????? ??????????????? ????????????
			                    	//str = new String(readBuffer,"UTF-8");				// String???????????? ????????????

									datay = (int)readBuffer[0];
			            			//model.addElement(str);		
									//output.write(str.getBytes());						// ?????????????????? ????????????
									datam.addbyte(readBuffer);

									//  ?????????????????? byte ?????????????????? ????????????
					    			for (int n=0; n<8;n++) {
					    				if ((datay & 0x1)==1){	
											output.write("1".getBytes());						// ?????????????????? ????????????
					        			}
					        			else if ((datay & 0x1)==0){ 	

					        				output.write("0".getBytes());						// ?????????????????? ????????????
					        			}

					    				datay=datay>>>1;
					    			}
					    			
					    			
									pattern3();
									
									
									
			                    }
			                }
			                catch (IOException e) {  
			                }  
			            } 
			            
			    }});  
			}   
			catch (TooManyListenersException e2) {  
			    e2.printStackTrace();  
			}
											// ?????????????????? ????????????
			Connectbtn.setText("Disconnect");											// Disconnect?????? ????????????
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
			model.clear();
			model2.clear();
			//model.addElement("???????????? ??????");		

			/*
			graph=new graphs("sensor");
			graph.pack();
	        RefineryUtilities.centerFrameOnScreen(graph);
	        graph.setVisible(true);
	        */
	        	        
		} else if (Connectbtn.getText().equals("Disconnect")) {
			serialPort.close();
			serialPort = null;  
			output.close();  
			output2.close();
			//model.addElement("???????????? ?????????");												// ?????????????????? ????????????
			Connectbtn.setText("Connect");	
			// Connect?????? ????????????
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());

	       // graph.setVisible(false);
	        
		}
    }
 // ?????????????????? ?????????????? ?????????????????? ????????????????????????  ?????? ??????????????????.   
    public void SendData( String str ){  
        try {
        	if (Connectbtn.getText()=="Disconnect"){
	            serialOut.write( str.getBytes() );  
	            serialOut.flush();  
        	}
        } catch (IOException e) {  
            e.printStackTrace();  
        }    
    }
 // ?????????????????? ?????????????? ?????????????????? ????????????????????????  ?????? ??????????????????.   
    public void SendNum( byte num ){  
        try {
        	if (Connectbtn.getText()=="Disconnect"){
	            serialOut.write( num );  
	            serialOut.flush();  
        	}
        } catch (IOException e) {  
            e.printStackTrace();  
        }    
    }
    
  /*  public void pattern2() throws IOException{
	    	if (datam.repattern2()){
	    		
	
				if (datam.data_prebit ==1){ 
					data_repattern2=data_repattern2 >>> 1;
					data_repattern2=data_repattern2 | 0x80000000;
				}
				else if (datam.data_prebit ==0){
					data_repattern2=data_repattern2 >>> 1;
				}
				
	    		if (num_monitor<110) {
	    			str_monitor=str_monitor + datam.data_receivebit;
	    			num_monitor++;
	    		} else {
	
					//model.addElement(str_monitor);
					scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
					str_monitor="";
		    		num_monitor=0;
	    		}
	    		
	
	
	    		if (datam.compare_print(data_repattern2)) {
	    			dataf=data_repattern2;
	    			printdata=" ";
	
	    			for (int n=0; n<32;n++) {
	    				if ((dataf & 0x1)==1){
	    					printdata=printdata + "1";
	        			}
	        			else if ((dataf & 0x1)==0){ 
	    					printdata=printdata + "0";
	        			}
	
	    				if (n==15) printdata=printdata + " ; "; 
	    				dataf=dataf>>>1;
	    			}
	    			
	    			
	    			//model2.addElement(printdata);
					scrollPane2.getVerticalScrollBar().setValue(scrollPane2.getVerticalScrollBar().getMaximum());
	    		}
	    		
	    		//output2.write(datam.data_receivebit.getBytes());
	    		if (findready<=0) {									// ??????????????? ????????????????????? ??????
	    			if (datam.compare(data_repattern2)){							// ??????????????????????????? ????????????
	    				if (!findpattern){
	    					
	
	    	    			dataf=data_repattern2;
	    	    			datam.data_receive=0;
	
	    	    			for (int n=0; n<16;n++) {
	    	    				dataf=dataf>>>1;
	    	    			}
	    	    			for (int n=0; n<15;n++) {
	    	    				if ((dataf & 0x1)==1){
	        	    				datam.data_receive=datam.data_receive << 1;
	        	    				datam.data_receive++;
	    	        			}
	    	        			else if ((dataf & 0x1)==0){ 
	        	    				datam.data_receive=datam.data_receive << 1;
	    	        			}
	
	    	    				dataf=dataf>>>1;
	    	    			}
	    	    			
	    					adddata(Integer.toString(datam.data_receive));
	    					model2.addElement(printdata + ";" + Integer.toString(datam.data_receive));
	    					
	    					if (wstart==0) {
	    						wstart=1;
	    					} else{
	    						output2.write("\n".getBytes());						// ?????????????????? ????????????
	    					}
	    					output2.write(Integer.toString(datam.data_receive).getBytes());
							output2.write(",".getBytes());						// ?????????????????? ????????????
	    					output2.write(Long.toString(System.currentTimeMillis()).getBytes());
	    					
	    					
	
	    					datam.data_receive=0;
						findready=1;								// ??????????????? ???????????????????????? ???????????? ????????? ???????????? ?????????????????? ????????????
						}
	    			} 
	    		} else {
	    			if (!findpattern){
	    				if (findready==30) { 							// ??????????????? ???????????????????????? 10???????????? ???????????????????????? ????????????
	    					//findpattern=true;
	    					findready=0;
	    				} else{
	    					findready++;								
	    				}
	    			}
	    		}
	    	
    		
    		
    	}
    }*/


public void pattern3() throws IOException{

	for (int nn=0; nn<8;nn++) {
		datam.dataraw=datam.dataraw>>>1;
//		if (datam.repattern3()){		//modified_180322

			long mkpattern1=1;
			mkpattern1=mkpattern1<<63;
			
			//data_repattern2=datam.data_repattern;	//modified_180322
			datam.data_repattern=datam.dataraw;		//added_180322
			data_repattern2=datam.dataraw;			//added_180322
			// 110????????? ??????????????? ???????????? ????????? ?????? ?????????
				if (num_monitor<110) {
					str_monitor=str_monitor + datam.data_receivebit;
					num_monitor++;
				} else {
		
					//model.addElement(str_monitor);
					scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
					str_monitor="";
		    		num_monitor=0;
				}
			
	
			
			
			//output2.write(datam.data_receivebit.getBytes());
			if (findready<=0) {									// ??????????????? ????????????????????? ??????
				if (datam.compare(data_repattern2)){							// ??????????????????????????? ????????????

		    			dataf=data_repattern2;
		    			printdata="";

		    			for (int n=0; n<32;n++) {
		    				if ((dataf & 0x1)==1){
		    					printdata=printdata + "1";
		        			}
		        			else if ((dataf & 0x1)==0){ 
		    					printdata=printdata + "0";
		        			}
		
		    				if (n==16) printdata=printdata + " ; "; 	//15 -> 16 modified 180322
		    				dataf=dataf>>>1;
		    			}

		    			dataf=data_repattern2;
		    			datam.data_receive=0;
		    			
		    			//Header 17-bit ??????. data-15bit??? dataf ?????? ??????????????? ??????.
		    			for (int n=0; n<17;n++) {	//16 -> 17 modified_180322
		    				dataf=dataf>>>1;		
		    			}
		    			
		    			// data-15bit -> datam.data_receive??? ??????.
		    			for (int n=0; n<15;n++) {
		    				if ((dataf & 0x1)==1){
	    	    				datam.data_receive=datam.data_receive << 1;
	    	    				datam.data_receive++;
		        			}
		        			else if ((dataf & 0x1)==0){ 
	    	    				datam.data_receive=datam.data_receive << 1;
		        			}
	
		    				dataf=dataf>>>1;		
		    			}
		    			
		    			//?????? ???(num_pre)??? ???????????? ???, ????????? ????????? ?????? ?????? ?????? ????????? ??????.
		    			if ((datam.data_receive<num_pre*1.5) & (datam.data_receive>num_pre/2) ) {
							
		    				adddata(Integer.toString(datam.data_receive));
							model2.addElement(printdata + ";" + Integer.toString(datam.data_receive));
							scrollPane2.getVerticalScrollBar().setValue(scrollPane2.getVerticalScrollBar().getMaximum());
							
							if (wstart==0) {
								wstart=1;
							} else{
								output2.write("\n".getBytes());						// ?????????????????? ????????????
							}
							output2.write(Integer.toString(datam.data_receive).getBytes());
							output2.write(",".getBytes());						// ?????????????????? ????????????
							output2.write(Long.toString(System.currentTimeMillis()).getBytes());
							num_pre=datam.data_receive;
		    			} else {
							
							model2.addElement("x "+printdata + ";" + Integer.toString(datam.data_receive));
							scrollPane2.getVerticalScrollBar().setValue(scrollPane2.getVerticalScrollBar().getMaximum());
							
							num_pre=datam.data_receive;
		    				
		    			}
	
						datam.data_receive=0;
						findready=1;	
	
				} else if (datam.compare_print(data_repattern2)) {				//  10100000???????????? ???????????????????????? ??????????????? ??????????????? ?????????????????????
					dataf=data_repattern2;
					printdata=" ";
	
					for (int n=0; n<32;n++) {
						if ((dataf & 0x1)==1){
							printdata=printdata + "1";
		    			}
		    			else if ((dataf & 0x1)==0){ 
							printdata=printdata + "0";
		    			}
	
						if (n==16) printdata=printdata + " ; ";		//15 -> 16 modified_180322 
						dataf=dataf>>>1;
					}
					
					
					model2.addElement(printdata);
					scrollPane2.getVerticalScrollBar().setValue(scrollPane2.getVerticalScrollBar().getMaximum());
				}
				
			} else {
				if (!findpattern){
					if (findready==10) { 							// ??????????????? ???????????????????????? 10???????????? ???????????????????????? ????????????
						//findpattern=true;
						findready=0;
					} else{
						findready++;								
					}
				}
			}
//		}		//modified_180322
		
	}

}
}
