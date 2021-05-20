import java.io.IOException;
import java.time.LocalDateTime;


import ithakimodem.Modem;

public class userApplication extends arqPackages {
	public static String echo_request_code = "E4800\r";
	public static String image_free_request_code = "M1839\r" ;
	public static String image_error_request_code = "G1420\r";
	public static String gps_request_code = "P0216"; 
	public static String ACK_request_code = "Q1284\r";
	public static String NACK_request_code = "R2509\r";

	public static void main(String[] args) throws IOException {
		(new userApplication()).applications();
	}
	
	
// Initialize modem
	public static Modem modem_initialization() {
		Modem modem;
		modem = new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(2000);
		modem.open("ithaki");
		String stranger = "";
		int j;
		for (;;) {
			j = modem.read();
			if (j != -1)
				stranger += (char) j;
			else
				break;
			if (stranger.indexOf("\r\n\n\n") > -1) {
				break;
			}
		}
		return modem;
	}

	public void applications() throws IOException {
		System.out.println("The Session has started at " + LocalDateTime.now());
		Modem modem = modem_initialization();
			////// get Echo Packages 
			echoPackages(modem);
			///// ARQ
			ARQerror(modem);
			////// Image Without Error
			 imageWithoutError(modem);
			////// Image With Error
			 imageWithError(modem);
			////// R Gps Request
			 gpsCoordinatesR(modem, gps_request_code);
		
		System.out.println("The Session has ended at " + LocalDateTime.now());

	}
}
