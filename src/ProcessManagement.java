
// GUI-related imports

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// File-related imports

import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.util.Arrays;

public class ProcessManagement extends Frame implements ActionListener
{
	// File Parameters
	String DataFilePath = null;
	String DataFileName = null;
		
	//Array
	Font font1 = new Font("Calibri",Font.BOLD, 16);
	Font font2 = new Font("Calibri",Font.BOLD, 12);
	
	int[] ProcessTime = new int[1000];
	int[] ArrivalTime = new int[1000];
	int[] WaitTime = new int[1000];
	int[] TurnaroundTime = new int[1000];
	int[] Processed   = new int[1000];
		// number of data items and keys
	 
	int time;
	int TimeQuantum = 100;
	boolean considerArrivalTime;
	boolean considerSwitchTime;
	int NumberOfDataItems=0;
	int contextTime = 5;
	
	// Statistics
	float WaitTimeFinal =0;
	float TRTTimeFinal =0;
	
	float FCFSaverageWaitTime     = 0;
	
	float SJNaverageWaitTime      = 0;
	
	float RRaverageWaitTime       = 0;
	
	
	float FCFSaverageTRTime     = 0;
	
	float SJNaverageTRTime      = 0;
	
	float RRaverageTRTime       = 0;
		
	String command = "";
		
	public static void main(String[] args)
	{
		Frame frame = new ProcessManagement();
		
			
		frame.setResizable(false);
		frame.setSize(1150,800);
		frame.setVisible(true);
		
		Font f1= new Font("Calibri",Font.BOLD, 12);
		frame.setFont(f1);
		
	}
	
	public ProcessManagement()
	{
		setTitle("Process Management Algorithms");
		
		// Create Menu Bar
		   			
		MenuBar mb = new MenuBar();
		setMenuBar(mb);
		
		// Create Menu Group Labeled "File"
		
		Menu FileMenu = new Menu("File");
		
		// Add it to Menu Bar
		
		mb.add(FileMenu);
		
		// Create Menu Items
		// Add action Listener 
		// Add to "File" Menu Group
		
		MenuItem miOpen = new MenuItem("Open Data File");
		miOpen.addActionListener(this);
		FileMenu.add(miOpen);
		
		MenuItem miAbout = new MenuItem("About");
		miAbout.addActionListener(this);
		FileMenu.add(miAbout);
						
		MenuItem miExit = new MenuItem("Exit");
		miExit.addActionListener(this);
		FileMenu.add(miExit);

		// Create Menu Group Labeled "File"
		
		Menu AlgMenu = new Menu("Algorithms");
		
		// Add it to Menu Bar
		
		mb.add(AlgMenu);
		
		// Create Menu Items
		// Add action Listener 
		// Add to "Search" Menu Group
		
		MenuItem miFCFS = new MenuItem("First Come First Served");
		miFCFS.addActionListener(this);
		AlgMenu.add(miFCFS);
						
		MenuItem miSJN = new MenuItem("Shortest Job Next");
		miSJN.addActionListener(this);
		AlgMenu.add(miSJN);
		
		MenuItem miRR = new MenuItem("Round Robin");
		miRR.addActionListener(this);
		AlgMenu.add(miRR);	
		
		MenuItem miAll = new MenuItem("Run All");
		miAll.addActionListener(this);
		AlgMenu.add(miAll);	
		
		
        
		WindowListener l = new WindowAdapter()
		{
						
			public void windowClosing(WindowEvent ev)
			{
				System.exit(0);
			}
			
			public void windowActivated(WindowEvent ev)
			{
				repaint();
			}
			
			public void windowStateChanged(WindowEvent ev)
			{
				repaint();
			}
		
		};
		
		ComponentListener k = new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e) 
			{
        		repaint();           
    		}
		};
		
		// register listeners
			
		this.addWindowListener(l);
		this.addComponentListener(k);

	}
	

//  called by windows manager whenever the application window performs an action select a menu item, close, resize, ....


	public void actionPerformed (ActionEvent ev)
		{
			// figure out which command was issued
			
			command = ev.getActionCommand();
			
			// take action accordingly
						
			if("Open Data File".equals(command))
			{
				
				DataFilePath = null;
				DataFileName = null;
				
				  JFileChooser chooser = new JFileChooser();
				  chooser.setDialogType(JFileChooser.OPEN_DIALOG );
				  chooser.setDialogTitle("Open Data File");
			      
			      int returnVal = chooser.showOpenDialog(null);
			      if( returnVal == JFileChooser.APPROVE_OPTION) 
			      	{
			          DataFilePath = chooser.getSelectedFile().getPath();
			          DataFileName = chooser.getSelectedFile().getName();
			        }
			      // read data file and copy it to original array
			      try
			      {
			    	  NumberOfDataItems = ReadFileIntoArray(DataFilePath,"Original");
			      }
			      catch (IOException ioe)
			      {
			    	  System.exit(0); 
			      }
				repaint();		
			}
			
			else
				if("Exit".equals(command))
				{
					System.exit(0);
				}
			else
				if("First Come First Served".equals(command))
				{
					Initialize();
					FCFS();
					repaint();
				}
			
			else
				if("Shortest Job Next".equals(command))
				{
					Initialize();
					SJN();
					repaint();
				}
			else
					
			if("Round Robin".equals(command))
			{
				Initialize();
				RoundRobin();			
				repaint();
			}
			
			else	
			if("Run All".equals(command))
			{
				Initialize();
				FCFS();
		
				Initialize();
				SJN();
				
				Initialize();
				RoundRobin();
				
				repaint();
			}
			else
				if("About".equals(command))
				{
					repaint();
				}
			
		}

// called by repaint() to redraw the screen

		
		public void paint(Graphics g)
		{
			GraphicsEnvironment e =
					GraphicsEnvironment.getLocalGraphicsEnvironment();
					String[] fontnames = e.getAvailableFontFamilyNames();
			/*		for (int i = 0; i < fontnames.length; i++)
					System.out.println(fontnames[i]); */
									
			if("Open Data File".equals(command))
			{
				// Acknowledge that file was opened
				if (DataFileName != null)
				{
					g.drawString("File --  "+DataFileName+"  -- was successfully opened", 300, 200);
					g.drawString("Number of Data Items = "+Integer.toString(NumberOfDataItems), 330, 250);
				}
				else
				{
					g.drawString("NO Data File is Open", 300, 200);
				}
				
				return;	
			}
			
			if("First Come First Served".equals(command) )
			{
				DisplayResults(g, "First Come First Served (FCFS)", FCFSaverageWaitTime, FCFSaverageTRTime);
			}
			
			if("Shortest Job Next".equals(command) )
			{
				DisplayResults(g, "Shortest Job Next (SJN)", SJNaverageWaitTime, SJNaverageTRTime);
					
			}
			else
			
			if("Round Robin".equals(command) )
			{
				DisplayResults(g, "Round Robin (RR)", RRaverageWaitTime, RRaverageTRTime);
			}
			
			if("Run All".equals(command) )
			{
				
			DisplayAll(g, FCFSaverageTRTime, FCFSaverageWaitTime,SJNaverageWaitTime,SJNaverageTRTime,RRaverageWaitTime, RRaverageTRTime   );
			}
			if("About".equals(command))
			{
				DisplayAbout(g);
			}
		}
		
public int ReadFileIntoArray(String filePath , String type) throws IOException
{
	if (filePath != null)
    {
  	  int index = 0;
  	  Scanner integerTextFile = new Scanner(new File(filePath));	 
  	  while (integerTextFile.hasNext())
  	  {
  		  int i = integerTextFile.nextInt();
  		  ProcessTime[index] = integerTextFile.nextInt();
  		  ArrivalTime[index] = integerTextFile.nextInt();
  		  index++;
  	  }
			//  end of file detected
  	  integerTextFile.close();
  	  return index ;

    }
	else
		return 0;
}



public void findTotals()
{
	WaitTimeFinal = 0;
	TRTTimeFinal = 0;
	
	for( int i = 0; i < NumberOfDataItems; i++)
	{
		WaitTimeFinal += WaitTime[i];
		TRTTimeFinal += TurnaroundTime[i];
	}
}

public void FCFSAvg ()
{
	findTotals();
	FCFSaverageWaitTime = WaitTimeFinal/NumberOfDataItems; 
	FCFSaverageTRTime = TRTTimeFinal/NumberOfDataItems; 
	
}

public void SJNAvg ()
{
	findTotals();
	SJNaverageWaitTime = WaitTimeFinal/NumberOfDataItems; 
	SJNaverageTRTime = TRTTimeFinal/NumberOfDataItems; 
	
}

public void RoundRobinAvg ()
{
	findTotals();
	RRaverageWaitTime = WaitTimeFinal/NumberOfDataItems; 
	RRaverageTRTime = TRTTimeFinal/NumberOfDataItems; 
	
}

public void FCFS() 
{
	for(int n =0; n< NumberOfDataItems; n++)
	{
		WaitTime[n] = time - ArrivalTime[n];
		time += ProcessTime[n];
		Processed[n] = time;
		TurnaroundTime[n] = time - ArrivalTime[n];
		time += contextTime;
		
	}
	FCFSAvg();
}

public void SJN() 
{	
	int z = 0;
	time = ProcessTime[0];
	Processed[0] = time;
	TurnaroundTime[0] = time;
	time += contextTime;
	for(int i = 1; i < NumberOfDataItems; i++)
	{
		z = FindNextShortest(time);
		WaitTime[z] = time - ArrivalTime[z];
		time += ProcessTime[z];
		Processed[z] = time;
		TurnaroundTime[z] = Processed[z] - ArrivalTime[z];
		time += contextTime;
	}
	SJNAvg();
	
}

public void RoundRobin() 
{
	int [] ProcessTime2 = new int[1000]; 
	System.arraycopy( ProcessTime, 0, ProcessTime2, 0, NumberOfDataItems); 
	int Items = 0;	
	
	while(Items < NumberOfDataItems*1.5)
	{
		for(int k = 0; k <= NumberOfDataItems; k++)
		{
			while(ProcessTime2[k] == 0 && k < NumberOfDataItems - 1)
			{
				k++;
			}
			if(ProcessTime2[k] == ProcessTime[k])
			{
				WaitTime[k] = time - ArrivalTime[k];
			}
			if(ProcessTime2[k] <= TimeQuantum)
			{
				time += ProcessTime2[k];
				Items ++;
				ProcessTime2[k] = 0;
				Processed[k] = time;
				TurnaroundTime[k] = Processed[k] - ArrivalTime[k];
				time += contextTime;	
			}
			else
			{
				time += TimeQuantum;
				ProcessTime2[k] -= TimeQuantum;
				time += contextTime;
			}
		}	
	}

	RoundRobinAvg();
	
}

public void Initialize()
{
	time=0;
	
	for (int i=0; i<NumberOfDataItems; i++)
	{
		Processed[i] = 0;
		WaitTime[i]=0;
		TurnaroundTime[i]=0;
	}
	
}
private int FindNextShortest(int time)
{
    int unprocessedIndex = 0;
    int shortest =0;
    int index =0;
    
    while(Processed[unprocessedIndex] != 0 && unprocessedIndex < NumberOfDataItems)
    {
    	unprocessedIndex++;
    }
    
     shortest = ProcessTime[unprocessedIndex];
     index = unprocessedIndex;
    
    for(int i = unprocessedIndex; i < NumberOfDataItems; i++)
    {
    	if(ArrivalTime[i] < time)
    	{
    		if(ProcessTime[i] < shortest && Processed[i] == 0)
    		 {
    			index = i;
    			shortest = ProcessTime[i];
    		 }
    	}
    }
    return index;
}

public void DisplayResults(Graphics g, String title, float avgWait, float avgTrnd)
{
	Font font1= new Font("Calibri", Font.BOLD, 16);
	g.setFont(font1);
	g.setColor(Color.red);
	g.drawString("Number Of Processes  = "+Integer.toString(NumberOfDataItems),500, 80);
	g.drawString("Total Period of Time = "+Integer.toString(time),500, 100);
	g.drawString("Time Quantum: 100 ", 500, 120);
	g.drawString("Context Switching Overhead: 5", 500, 140);
	int k = (this.getWidth()- (title.length()+20)*6)/2;
	g.drawString("Scheduling Policy: "+title,k, 160);
	Font font2 = new Font ("Calibri", Font.BOLD, 12);
	g.setFont(font2);
	g.setColor(Color.BLACK);
	int x = 50;
	int y = 200;
	for (int i=0; i<3; i++)
	{
		g.drawString("ID", x, y);
		g.drawString("PTime", x+40, y);
		g.drawString("ArrTime", x+100, y);
		g.drawString("Wait Time", x+165, y);
		g.drawString("TurnAround Time", x+230, y);
		y=y+15;
		g.drawLine(x, y, x+320, y);
		
		for (int j=0; j<30; j++)
		{
			y=y+15;
			g.drawString(Integer.toString(i*30+j), x, y);
			g.drawString(Integer.toString(ProcessTime[i*30+j]), x+40, y);
			g.drawString(Integer.toString(ArrivalTime[i*30+j]), x+100, y);
			g.drawString(Integer.toString(WaitTime[i*30+j]), x+170, y);
			g.drawString(Integer.toString(TurnaroundTime[i*30+j]), x+250, y);
		}
		x=x+360;
		y=200;
	}
	g.setFont(font1);
	g.setColor(Color.RED);
	g.drawString("Average Wait Time ", 450, 730);
	g.drawString("Average TurnAround Time", 600, 730);
	g.drawLine(400, 745, 800, 745);
	g.setColor(Color.BLACK);
	g.drawString(Float.toString(avgWait), 480, 760);
	g.drawString(Float.toString(avgTrnd), 650, 760);	
}
 public void DisplayAll (Graphics g, float fCFSaverageTRTime2, float fCFSaverageWaitTime2, float sJNaverageWaitTime2, float sJNaverageTRTime2, float rRaverageWaitTime2, float rRaverageTRTime2)
 {
	 Font font1= new Font("Calibri", Font.BOLD, 16);
	 g.setFont(font1);
	 int x =400;
	 int y = 200;
	 g.drawString("FCFS",x, y); 
	 g.drawString("" +FCFSaverageWaitTime, x+250, y);
	 g.drawString("" +FCFSaverageTRTime, x+400, y);
	 g.drawString("SJN", x, y+20);
	 g.drawString("" + SJNaverageWaitTime, x+250,y+20);
	 g.drawString("" + SJNaverageTRTime, x+400,y+20);
	 g.drawString("Round Robin", x, y+40);
	 g.drawString("" + RRaverageWaitTime, x+250,y+40);
	 g.drawString("" + RRaverageTRTime, x+400,y+40);
	 g.drawString("Average Wait Time", x+250, y-30);
	 g.drawString("Average Turn Around Time", x+400, y-30);
 }
 
 public void DisplayAbout(Graphics g)
 {
	 g.drawString("This program runs processor scheduling algorithms and displays statistics about the results.",300,400);
 }
 
}