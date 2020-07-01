package me.przemovi;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortTimeoutException;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.exceptions.UnableToConnectException;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Main {
	private JLabel checkEng;
	private JLabel checkBat;
	private JLabel checkConn;

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	try {
					new Main().start();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        });
	}
	
	public void setCheckEngine(boolean b) {
		try {
			checkEng.setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream(b ? "/files/check-engine.png" : "/files/check-engine-off.png"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setCheckBat(boolean b) {
		try {
			checkBat.setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream(b ? "/files/check-bat.png" : "/files/check-bat-off.png"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setCheckConn(boolean b) {
		try {
			checkConn.setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream(b ? "/files/check-connection.png" : "/files/check-connection-off.png"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() throws IOException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		JFrame frame = new JFrame();
		frame.getContentPane().setBackground(Color.BLACK);
		
		frame.setBounds(0, 0, 800, 480);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setAlwaysOnTop(true);
		frame.setUndecorated(true);
		//frame.createBufferStrategy(2);
		
		checkEng = new JLabel(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/files/check-engine-off.png"))));
		checkEng.setBounds(263, 385, 80, 55);
		frame.getContentPane().add(checkEng);
		
		checkBat = new JLabel(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/files/check-bat-off.png"))));
		checkBat.setBounds(353, 385, 80, 55);
		frame.getContentPane().add(checkBat);
		
		checkConn = new JLabel(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/files/check-connection-off.png"))));
		checkConn.setBounds(173, 385, 80, 55);
		frame.getContentPane().add(checkConn);
		
		Gauge g = new Gauge();
		frame.getContentPane().add(g);
		g.setBounds(0, 0, 480, 480);
		//g.setBorder(BorderFactory.createLineBorder(Color.RED));
		g.setMaximum(1000);
		
		TripleBar water_temp = new TripleBar();
		water_temp.setBounds(490, 27, 300, 100);
		frame.getContentPane().add(water_temp);
		water_temp.setDescription("WATER TEMP");
		water_temp.setMin(40);
		water_temp.setMax(120);
		water_temp.setValName("C");
		
		TripleBar oil_temp = new TripleBar();
		oil_temp.setBounds(490, 138, 300, 100);
		frame.getContentPane().add(oil_temp);
		oil_temp.setDescription("INTAKE AIR");
		oil_temp.setMin(0);
		oil_temp.setMax(90);
		oil_temp.setValName("C");
		
		TripleBar engLoad = new TripleBar();
		engLoad.setBounds(490, 258, 300, 100);
		frame.getContentPane().add(engLoad);
		engLoad.setDescription("LOAD");
		engLoad.setMin(0);
		engLoad.setMax(100);
		engLoad.setValName("% ");
		
		TripleBar fuel_level = new TripleBar();
		fuel_level.setBounds(490, 369, 300, 100);
		frame.getContentPane().add(fuel_level);
		fuel_level.setDescription("FUEL LEVEL");
		fuel_level.setMin(0);
		fuel_level.setMax(100);
		fuel_level.setValName("%");
		
		frame.setVisible(true);
		
		if (ge.getDefaultScreenDevice().getDisplayMode().getWidth() == 800) {
			for (Window w : Window.getWindows()) {
				ge.getDefaultScreenDevice().setFullScreenWindow(w);
			}
		}
		
				/*final GpioController gpio = GpioFactory.getInstance();
		        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_17, PinPullResistance.PULL_DOWN);
		        final GpioPinDigitalOutput myDisp = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27);
		        
		        myDisp.setState(true);*/
		        
		        /*myButton.setShutdownOptions(true);
		        
		        myButton.addListener(new GpioPinListenerDigital() {
		            @Override
		            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
		            }

		        });*/

		final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput carPowerSense = gpio.provisionDigitalInputPin(RaspiPin.GPIO_17, PinPullResistance.PULL_DOWN);
        final GpioPinDigitalOutput display = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27);
        display.setState(false);
        carPowerSense.setShutdownOptions(true);
        carPowerSense.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent e) {
				System.out.println(e.getState());
				display.setState(e.getState().isLow());
			}
        });
        
        
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				boolean failure = false;
				boolean initiated = false;
				
				SerialPort port;
				if (ge.getDefaultScreenDevice().getDisplayMode().getWidth() == 800) {
					port = SerialPort.getCommPort("/dev/ttyUSB0");
				} else {
					port = SerialPort.getCommPort("COM5");
				}
				
				port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 2000);
				port.setComPortParameters(
					      38400,
					      8,  // data bits
					      SerialPort.ONE_STOP_BIT,
					      SerialPort.NO_PARITY);
				port.openPort();
				
				g.setMaximum(250);
				
				
				
				int s = 0;
				while (!Thread.interrupted()) {
					try {
						if (!initiated) {
							Thread.sleep(2000);
							new EchoOffCommand().run(port.getInputStream(), port.getOutputStream());
							new LineFeedOffCommand().run(port.getInputStream(), port.getOutputStream());
							new TimeoutCommand(125).run(port.getInputStream(), port.getOutputStream());
							//new SelectProtocolCommand(ObdProtocols.AUTO).run(port.getInputStream(), port.getOutputStream());
							Thread.sleep(2000);
							initiated = true;
						}
						com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand x = new IntakeManifoldPressureCommand();
						x.run(port.getInputStream(), port.getOutputStream());
						int r = x.getMetricUnit();
						int y = Math.max(50, r)-50;
						g.setValue(y);
						
						if(failure) {
							failure = false;
							setCheckConn(false);
						}

						if (s == 0) {
							com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand coolantTempCmd = new EngineCoolantTemperatureCommand();
							coolantTempCmd.run(port.getInputStream(), port.getOutputStream());
							water_temp.setValue(coolantTempCmd.getTemperature());
							s++;
						} else if (s == 1) {
							com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand oilTempCmd = new AirIntakeTemperatureCommand();
							oilTempCmd.run(port.getInputStream(), port.getOutputStream());
							oil_temp.setValue(oilTempCmd.getTemperature());
							s++;
						} else if (s == 2) {
							com.github.pires.obd.commands.engine.ThrottlePositionCommand engLoadCmd = new ThrottlePositionCommand();
							engLoadCmd.run(port.getInputStream(), port.getOutputStream());
							engLoad.setValue(engLoadCmd.getPercentage());
							s++;
						} else if (s == 3) {
							com.github.pires.obd.commands.fuel.FuelLevelCommand fuelLvlCmd = new FuelLevelCommand();
							fuelLvlCmd.run(port.getInputStream(), port.getOutputStream());
							fuel_level.setValue(fuelLvlCmd.getPercentage());
							s = 0;
						}
						//System.out.println(r);
					} catch (SerialPortTimeoutException|UnableToConnectException e) {
						failure = true;
						initiated = false;
						setCheckConn(true);
					} catch (Exception e) {
						e.printStackTrace();
						
						failure = true;
						initiated = false;
						setCheckConn(true);
					}
				}
			}
		}).start();
	}
}
