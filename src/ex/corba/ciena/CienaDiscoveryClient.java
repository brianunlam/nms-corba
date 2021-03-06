package ex.corba.ciena;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciena.oc.emsSession.EmsSession_I;
import com.ciena.oc.globaldefs.ProcessingFailureException;

import ex.corba.ciena.error.CorbaErrorProcessor;
import ex.corba.ciena.transform.sax.Corba2XMLHandler;

public class CienaDiscoveryClient extends CienaConnection {
	public static final Logger LOG = LoggerFactory.getLogger(CienaDiscoveryClient.class);

	protected static Corba2XMLHandler handler;

	public static void main(String args[]) {
		CienaDiscoveryClient main = new CienaDiscoveryClient();
		EmsSession_I emsSession = null;

		try {
			emsSession = main.openEmsSession(args);

			// main.executeCommands(emsSession);
			main.executeCommandsXmlOutput(emsSession);
		} catch (ProcessingFailureException prf) {
			LOG.error("Ciena ON-Center: " + CorbaErrorProcessor.printError(prf));
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			main.closeEmsSession(emsSession);
		}
	}

	public void executeCommands(EmsSession_I emsSession) throws Exception {
		CorbaCommands cmd = new CorbaCommands(emsSession, realEMSName);

		cmd.getAllManagedElementNames();
	}

	public void executeCommandsXmlOutput(EmsSession_I emsSession) throws Exception {
		OutputFormat format = OutputFormat.createPrettyPrint();
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream("ciena-" + sdf.format(currentDate) + ".xml"), format);

		handler = new Corba2XMLHandler(xmlWriter);

		CorbaCommands cmd = new CorbaCommands(emsSession, realEMSName, xmlWriter);

		handler.handlerBuilderStart();

		if (props.getProperty("getAllManagedElements") != null
				&& props.getProperty("getAllManagedElements").equalsIgnoreCase("yes")) {
			cmd.getAllManagedElements();
		}

		if (props.getProperty("getAllEquipment") != null
				&& props.getProperty("getAllEquipment").equalsIgnoreCase("yes")) {
			cmd.getAllEquipment();
		}

		if (props.getProperty("getAllPTPs") != null && props.getProperty("getAllPTPs").equalsIgnoreCase("yes")) {
			cmd.getAllPTPs();
		}

		if (props.getProperty("getAllTopologicalLinks") != null
				&& props.getProperty("getAllTopologicalLinks").equalsIgnoreCase("yes")) {
			cmd.getAllTopologicalLinks();
		}

		if (props.getProperty("getAllSubnetworkConnections") != null
				&& props.getProperty("getAllSubnetworkConnections").equalsIgnoreCase("yes")) {
			cmd.getAllSubnetworkConnections();
		}

		if (props.getProperty("getAllProtectionGroups") != null
				&& props.getProperty("getAllProtectionGroups").equalsIgnoreCase("yes")) {
			cmd.getAllProtectionGroups();
		}

		if (props.getProperty("getAllGTPs") != null && props.getProperty("getAllGTPs").equalsIgnoreCase("yes")) {
			cmd.getAllGTPs();
		}

		if (props.getProperty("getRoute") != null && props.getProperty("getRoute").equalsIgnoreCase("yes")) {
			cmd.getRoute();
		}

		if (props.getProperty("getAllFDFrs") != null && props.getProperty("getAllFDFrs").equalsIgnoreCase("yes")) {
			cmd.getAllFDFrs();
		}

		if (props.getProperty("getContainedInUseTPs") != null
				&& props.getProperty("getContainedInUseTPs").equalsIgnoreCase("yes")) {
			cmd.getContainedInUseTPs();
		}

		if (props.getProperty("getEquipmentConfiguration") != null
				&& props.getProperty("getEquipmentConfiguration").equalsIgnoreCase("yes")) {
			cmd.getEquipmentConfiguration();
		}

		handler.handlerBuilderEnd();
	}
}