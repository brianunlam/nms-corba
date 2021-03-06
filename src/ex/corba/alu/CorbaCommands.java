package ex.corba.alu;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.omg.CORBA.StringHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import common.Common_IHolder;
import emsMgr.EMSMgr_I;
import emsMgr.EMSMgr_IHelper;
import emsSession.EmsSession_I;
import equipment.EquipmentInventoryMgr_I;
import equipment.EquipmentInventoryMgr_IHelper;
import equipment.EquipmentOrHolderIterator_IHolder;
import equipment.EquipmentOrHolderList_THolder;
import ex.corba.CorbaConstants;
import ex.corba.alu.error.CorbaErrorDescriptions;
import ex.corba.alu.error.CorbaErrorProcessor;
import ex.corba.alu.transform.jaxb.Corba2Object;
import ex.corba.alu.transform.sax.Corba2XMLContainer;
import ex.corba.alu.transform.sax.Corba2XMLHandler;
import ex.corba.alu.transform.sax.Corba2XMLHelper;
import ex.corba.alu.transform.sax.Corba2XMLStructure;
import ex.corba.xml.EquipmentHolder;
import ex.corba.xml.JaxbOutputHandler;
import ex.corba.xml.ManagedElement;
import ex.corba.xml.NmsObjects;
import extendServiceMgr.ExtendServiceMgr_I;
import extendServiceMgr.ExtendServiceMgr_IHelper;
import flowDomain.FlowDomainMgr_I;
import flowDomain.FlowDomainMgr_IHelper;
import flowDomainFragment.FDFrIterator_I;
import flowDomainFragment.FDFrIterator_IHolder;
import flowDomainFragment.FDFrList_THolder;
import flowDomainFragment.FlowDomainFragment_T;
import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesIterator_IHolder;
import globaldefs.NamingAttributesList_THolder;
import globaldefs.ProcessingFailureException;
import managedElement.ManagedElementIterator_IHolder;
import managedElement.ManagedElementList_THolder;
import managedElement.ManagedElement_T;
import managedElementManager.ManagedElementMgr_I;
import managedElementManager.ManagedElementMgr_IHelper;
import multiLayerSubnetwork.EMSFreedomLevel_T;
import multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;
import protection.ProtectionGroupIterator_IHolder;
import protection.ProtectionGroupList_THolder;
import protection.ProtectionMgr_I;
import protection.ProtectionMgr_IHelper;
import subnetworkConnection.CrossConnect_T;
import subnetworkConnection.GradesOfImpact_T;
import subnetworkConnection.ProtectionEffort_T;
import subnetworkConnection.Route_THolder;
import subnetworkConnection.SNCCreateData_T;
import subnetworkConnection.SNCIterator_IHolder;
import subnetworkConnection.SNCModifyData_T;
import subnetworkConnection.SubnetworkConnectionList_THolder;
import subnetworkConnection.SubnetworkConnection_T;
import subnetworkConnection.SubnetworkConnection_THolder;
import subnetworkConnection.TPDataList_THolder;
import terminationPoint.TerminationPointIterator_IHolder;
import terminationPoint.TerminationPointList_THolder;
import terminationPoint.TerminationPoint_T;
import terminationPoint.TerminationPoint_THolder;
import topologicalLink.TopologicalLinkIterator_IHolder;
import topologicalLink.TopologicalLinkList_THolder;
import topologicalLink.TopologicalLink_T;

public class CorbaCommands {
	public static final String ME_MANAGER_NAME = "ManagedElement";
	public static final String EI_MANAGER_NAME = "EquipmentInventory";
	public static final String MLS_MANAGER_NAME = "MultiLayerSubnetwork";
	public static final String EMS_MANAGER_NAME = "EMS";
	public static final String PRT_MANAGER_NAME = "Protection";
	public static final String FLOW_DOMAIN_MANAGER = "FlowDomain";
	public static final String EXTEND_SERVICE_MANAGER = "ExtendService";

	public static final int HOW_MANY = 100;

	public static final Logger LOG = LoggerFactory.getLogger(CorbaCommands.class);

	private EmsSession_I emsSession;
	private String emsName;
	private Corba2XMLHandler handler;
	private Corba2XMLHelper helper;
	// private JaxbOutputHandler jaxbOutputHandler;

	private Common_IHolder managerInterface;
	private ManagedElementMgr_I meManager;
	private EquipmentInventoryMgr_I eiManager;
	private MultiLayerSubnetworkMgr_I mlsnManager;
	private EMSMgr_I emsManager;
	private ProtectionMgr_I protectionMgr;
	private FlowDomainMgr_I flowDomainMgr;
	private ExtendServiceMgr_I extendServiceMgr;

	// Cache list
	private List<String> neNames;
	private List<String> neNamesWithoutVNE;
	private List<String> sncNames;
	private List<String> subnetworkNames;
	private List<NameAndStringValue_T[]> serverTrailTPList;

	private Set<Short> terminationPointRates;

	public CorbaCommands(EmsSession_I emsSession, String emsName) {
		this.emsSession = emsSession;
		this.emsName = emsName;
	}

	public CorbaCommands(EmsSession_I emsSession, String emsName, ContentHandler contentHandler) {
		this.emsSession = emsSession;
		this.emsName = emsName;
		this.handler = new Corba2XMLHandler(contentHandler);
		this.helper = new Corba2XMLHelper(handler);
		// this.jaxbOutputHandler = new JaxbOutputHandler(contentHandler);
	}

	public boolean setManagerByName(final String managerName) throws ProcessingFailureException {

		this.managerInterface = new Common_IHolder();
		this.emsSession.getManager(managerName, this.managerInterface);

		if (managerName.equals(ME_MANAGER_NAME)) {
			if (this.meManager == null) {
				this.meManager = ManagedElementMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(EI_MANAGER_NAME)) {
			if (this.eiManager == null) {
				this.eiManager = EquipmentInventoryMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(MLS_MANAGER_NAME)) {
			if (this.mlsnManager == null) {
				this.mlsnManager = MultiLayerSubnetworkMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(EMS_MANAGER_NAME)) {
			if (this.emsManager == null) {
				this.emsManager = EMSMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(PRT_MANAGER_NAME)) {
			if (this.protectionMgr == null) {
				this.protectionMgr = ProtectionMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(FLOW_DOMAIN_MANAGER)) {
			if (this.flowDomainMgr == null) {
				this.flowDomainMgr = FlowDomainMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(EXTEND_SERVICE_MANAGER)) {
			if (this.extendServiceMgr == null) {
				this.extendServiceMgr = ExtendServiceMgr_IHelper.narrow(managerInterface.value);
			}
		} else {
			return false;
		}

		return true;
	}

	public List<String> getAllManagedElementNames() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllManagedElementNames() start.");
		}

		if (!setManagerByName(ME_MANAGER_NAME)) {
			return null;
		}

		NamingAttributesList_THolder meNameList = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder meNameItr = new NamingAttributesIterator_IHolder();

		this.meManager.getAllManagedElementNames(HOW_MANY, meNameList, meNameItr);

		neNames = new ArrayList<String>();

		NameAndStringValue_T[][] mes = meNameList.value;

		for (NameAndStringValue_T[] me : mes) {
			neNames.add(handler.getValueByName(me, "ManagedElement"));
		}

		boolean exitwhile = false;

		if (meNameItr.value != null) {
			try {
				boolean hasMoreData = true;
				while (hasMoreData) {
					hasMoreData = meNameItr.value.next_n(HOW_MANY, meNameList);
					mes = meNameList.value;

					for (NameAndStringValue_T[] me : mes) {
						neNames.add(handler.getValueByName(me, "ManagedElement"));
					}
				}

				exitwhile = true;
			} finally {
				if (!exitwhile)
					meNameItr.value.destroy();
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllManagedElementNames() complete.");
		}

		return neNames;
	}

	public void getAllManagedElements() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllManagedElements() start.");
		}

		if (!setManagerByName(ME_MANAGER_NAME)) {
			return;
		}

		ManagedElementList_THolder meList = new ManagedElementList_THolder();
		ManagedElementIterator_IHolder meItr = new ManagedElementIterator_IHolder();

		this.meManager.getAllManagedElements(HOW_MANY, meList, meItr);
		neNames = new ArrayList<String>();
		neNamesWithoutVNE = new ArrayList<String>();

		ManagedElement_T[] mes = meList.value;

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllManagedElements: got " + mes.length + " MEs ");
		}

		List<ManagedElement> managedElements = new ArrayList<ManagedElement>();

		for (ManagedElement_T me : mes) {
			handler.printStructure(helper.getManagedElementParams(me));
			// managedElements.add(Corba2Object.getManagedElement(me));
			neNames.add(handler.getValueByName(me.name, "ManagedElement"));

			// Cache actual NE without VNE or ENE
			// Product Name = 'External Network' or '' = VNE (SDH devices of
			// external network)
			// Product Name = 'None' = ENE (ISA Cards of external network)
			if (!me.productName.equals("External Network") && !me.productName.equals("")
					&& !me.productName.equals("None") && !me.productName.startsWith("ISA_ES")) {
				neNamesWithoutVNE.add(handler.getValueByName(me.name, "ManagedElement"));
			}
			// jaxbOutputHandler.printManagedElementContentHandler(Corba2Object
			// .getManagedElement(me));
		}

		boolean exitWhile = false;

		if (meItr.value != null) {
			try {
				boolean hasMoreData = true;
				while (hasMoreData) {
					hasMoreData = meItr.value.next_n(HOW_MANY, meList);
					mes = meList.value;

					if (LOG.isInfoEnabled())
						LOG.info("getAllManagedElements: got " + mes.length + " MEs ");

					for (ManagedElement_T me : mes) {
						handler.printStructure(helper.getManagedElementParams(me));
						managedElements.add(Corba2Object.getManagedElement(me));
						neNames.add(handler.getValueByName(me.name, "ManagedElement"));

						// Cache actual NE without VNE or ENE
						if (!me.productName.equals("External Network") && !me.productName.equals("")
								&& !me.productName.equals("None") && !me.productName.startsWith("ISA_ES")) {
							neNamesWithoutVNE.add(handler.getValueByName(me.name, "ManagedElement"));
						}
					}
				}

				exitWhile = true;
			} finally {
				if (!exitWhile)
					meItr.value.destroy();
			}
		}

		// Specific to JAXB XML output: Start
		// JaxbOutputHandler out = new JaxbOutputHandler("managedelement.xml");
		// NmsObjects nmsObjects = new NmsObjects();
		// nmsObjects.setManagedElements(managedElements);
		// out.print(nmsObjects);
		// Specific to JAXB XML output: End

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllManagedElements() complete.");
		}
	}

	public void getAllEquipment() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllEquipment() start.");
		}

		if (neNamesWithoutVNE == null) {
			getAllManagedElements();
		}

		if (!setManagerByName(EI_MANAGER_NAME))
			return;

		NameAndStringValue_T[] ne = new NameAndStringValue_T[2];

		ne[0] = new NameAndStringValue_T("EMS", emsName);
		ne[1] = new NameAndStringValue_T();
		ne[1].name = "ManagedElement";

		EquipmentOrHolderList_THolder equipOrHolderList = new EquipmentOrHolderList_THolder();
		EquipmentOrHolderIterator_IHolder equipOrHolderItr = new EquipmentOrHolderIterator_IHolder();

		int meCounter = 0;
		boolean exitWhile = false;

		for (String neName : neNamesWithoutVNE) {
			try {
				ne[1].value = neName;
				eiManager.getAllEquipment(ne, HOW_MANY, equipOrHolderList, equipOrHolderItr);

				if (LOG.isInfoEnabled()) {
					LOG.info("getAllEquipment: got " + equipOrHolderList.value.length + " equipments for ME "
							+ ne[1].value);
				}

				for (int i = 0; i < equipOrHolderList.value.length; i++) {
					helper.printEquipmentOrHolder(equipOrHolderList.value[i]);
				}

				exitWhile = false;

				if (equipOrHolderItr.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = equipOrHolderItr.value.next_n(HOW_MANY, equipOrHolderList);

							for (int i = 0; i < equipOrHolderList.value.length; i++) {
								helper.printEquipmentOrHolder(equipOrHolderList.value[i]);
							}
						}

						exitWhile = true;
					} finally {
						if (!exitWhile)
							equipOrHolderItr.value.destroy();
					}
				}

				meCounter++;

				if (LOG.isInfoEnabled()) {
					LOG.info("getAllEquipment: finished getEquipment for ME " + ne[1].value + " Order number # "
							+ meCounter);
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getAllEquipment. ME: " + neName);
			}
		}

		// printEquipmentHolderList(equipmentHolders);
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllEquipment() complete.");
		}
	}

	public void printEquipmentHolderList(List<EquipmentHolder> equipmentHolder) throws Exception {
		JaxbOutputHandler out = new JaxbOutputHandler("holders.xml");
		NmsObjects nmsObjects = new NmsObjects();
		nmsObjects.setEquipmentHolders(equipmentHolder);
		out.print(nmsObjects);
	}

	public void getAllPTPs() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllPTPs() start.");
		}

		if (neNames == null) {
			getAllManagedElements();
		}

		if (!setManagerByName(ME_MANAGER_NAME)) {
			return;
		}

		NameAndStringValue_T[] neNameArray = new NameAndStringValue_T[2];

		neNameArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		neNameArray[1] = new NameAndStringValue_T();
		neNameArray[1].name = CorbaConstants.MANAGED_ELEMENT_STR;

		TerminationPointList_THolder terminationPointList = new TerminationPointList_THolder();
		TerminationPointIterator_IHolder terminationPointIterator = new TerminationPointIterator_IHolder();
		short[] tpLayerRateList = new short[0];
		short[] connectionLayerRateList = new short[0];

		int counter = 0;
		boolean exitWhile = false;
		for (String neName : neNames) {
			try {
				neNameArray[1].value = neName;
				meManager.getAllPTPs(neNameArray, tpLayerRateList, connectionLayerRateList, HOW_MANY,
						terminationPointList, terminationPointIterator);

				if (LOG.isInfoEnabled()) {
					LOG.info("getAllPTPs: got {} PTP for ME {}.", terminationPointList.value.length,
							neNameArray[1].value);
				}

				for (int i = 0; i < terminationPointList.value.length; i++) {
					helper.printTerminationPoint(terminationPointList.value[i]);
				}

				exitWhile = false;

				if (terminationPointIterator.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = terminationPointIterator.value.next_n(HOW_MANY, terminationPointList);

							if (LOG.isInfoEnabled()) {
								LOG.info("getAllPTPs: got {} PTP for ME {}.", terminationPointList.value.length,
										neNameArray[1].value);
							}

							for (int i = 0; i < terminationPointList.value.length; i++) {
								helper.printTerminationPoint(terminationPointList.value[i]);
							}
						}
						exitWhile = true;
					} finally {
						if (!exitWhile) {
							terminationPointIterator.value.destroy();
						}
					}

					counter++;

					if (LOG.isInfoEnabled()) {
						LOG.info("getAllPTPs: finished getPTP for ME " + neNameArray[1].value + " Order number # "
								+ counter);
					}
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getAllPTPs. ME: " + neName);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllPTPs() complete.");
		}
	}

	public void getAllTopologicalLinks() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllTopologicalLinks() start.");
		}

		if (subnetworkNames == null) {
			subnetworkNames = getAllTopLevelSubnetworkNames();
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		NameAndStringValue_T[] mlsn = new NameAndStringValue_T[2];

		mlsn[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		mlsn[1] = new NameAndStringValue_T(CorbaConstants.MULTILAYER_SUBNETWORK_STR, "1");

		TopologicalLinkList_THolder topologicalLinkList = new TopologicalLinkList_THolder();
		TopologicalLinkIterator_IHolder topologicalLinkIterator = new TopologicalLinkIterator_IHolder();

		for (String subnetwork : subnetworkNames) {
			try {
				mlsn[1].value = subnetwork;
				mlsnManager.getAllTopologicalLinks(mlsn, HOW_MANY, topologicalLinkList, topologicalLinkIterator);

				for (int i = 0; i < topologicalLinkList.value.length; i++) {
					handler.printStructure(getTopologicalLinkParams(topologicalLinkList.value[i]));
				}

				boolean exitWhile = false;
				if (topologicalLinkIterator.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = topologicalLinkIterator.value.next_n(HOW_MANY, topologicalLinkList);
							for (int i = 0; i < topologicalLinkList.value.length; i++) {
								handler.printStructure(getTopologicalLinkParams(topologicalLinkList.value[i]));
							}
						}

						exitWhile = true;
					} finally {
						if (!exitWhile) {
							topologicalLinkIterator.value.destroy();
						}
					}
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getAllTopologicalLinks. MLSN: " + mlsn[1].value);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllTopologicalLinks() complete.");
		}
	}

	private Corba2XMLContainer getTopologicalLinkParams(TopologicalLink_T topologicalLink)
			throws ProcessingFailureException {
		Corba2XMLContainer container = new Corba2XMLContainer(Corba2XMLStructure.TOPOL_LINKS);

		container.setFieldValue(CorbaConstants.USER_LABEL_STR, topologicalLink.userLabel);
		container.setFieldValue(CorbaConstants.TL_ID_STR,
				handler.getValueByName(topologicalLink.name, "TopologicalLink"));
		container.setFieldValue(CorbaConstants.NATIVE_EMS_NAME_STR, topologicalLink.nativeEMSName);
		container.setFieldValue(CorbaConstants.OWNER_STR, topologicalLink.owner);
		container.setFieldValue(CorbaConstants.DIRECTION_STR, String.valueOf(topologicalLink.direction.value()));
		container.setFieldValue(CorbaConstants.RATE_STR, String.valueOf(topologicalLink.rate));
		container.setFieldValue(CorbaConstants.A_END_NE_STR,
				handler.getValueByName(topologicalLink.aEndTP, CorbaConstants.MANAGED_ELEMENT_STR));
		container.setFieldValue(CorbaConstants.A_END_TP_STR,
				handler.getValueByName(topologicalLink.aEndTP, CorbaConstants.PTP_STR));
		container.setFieldValue(CorbaConstants.Z_END_NE_STR,
				handler.getValueByName(topologicalLink.zEndTP, CorbaConstants.MANAGED_ELEMENT_STR));
		container.setFieldValue(CorbaConstants.Z_END_TP_STR,
				handler.getValueByName(topologicalLink.zEndTP, CorbaConstants.PTP_STR));

		TerminationPoint_T aEndTP = getTerminationPoint(topologicalLink.aEndTP, topologicalLink.rate);

		if (aEndTP != null) {
			container.setFieldValue(CorbaConstants.A_TRANSMISSION_PARAMS_STR,
					handler.convertLayeredParametersToString(aEndTP.transmissionParams));
		} else {
			container.setFieldValue(CorbaConstants.A_TRANSMISSION_PARAMS_STR, "");
		}

		TerminationPoint_T zEndTP = getTerminationPoint(topologicalLink.zEndTP, topologicalLink.rate);
		if (zEndTP != null) {
			container.setFieldValue(CorbaConstants.Z_TRANSMISSION_PARAMS_STR,
					handler.convertLayeredParametersToString(zEndTP.transmissionParams));
		} else {
			container.setFieldValue(CorbaConstants.Z_TRANSMISSION_PARAMS_STR, "");
		}

		container.setFieldValue(CorbaConstants.ADDITIONAL_INFO_STR,
				handler.convertNameAndStringValueToString(topologicalLink.additionalInfo));

		return container;
	}

	private TerminationPoint_T getTerminationPoint(NameAndStringValue_T[] endPointName, short rate)
			throws ProcessingFailureException {

		if (!setManagerByName(ME_MANAGER_NAME))
			return null;

		if (terminationPointRates == null
				|| (terminationPointRates != null && !terminationPointRates.contains(Short.valueOf(rate)))) {
			return null;
		}

		TerminationPoint_THolder tpHolder = new TerminationPoint_THolder();

		try {
			meManager.getTP(endPointName, tpHolder);
		} catch (ProcessingFailureException ex) {
			handleProcessingFailureException(ex,
					"getTerminationPoint. TP: " + handler.convertNameAndStringValueToString(endPointName));
		}

		return tpHolder.value;
	}

	public List<String> getAllTopLevelSubnetworkNames() throws ProcessingFailureException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllTopLevelSubnetworkNames() start.");
		}

		if (!setManagerByName(EMS_MANAGER_NAME)) {
			return null;
		}

		NamingAttributesList_THolder namingAttributesList = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder namingAttributesIterator = new NamingAttributesIterator_IHolder();

		emsManager.getAllTopLevelSubnetworkNames(HOW_MANY, namingAttributesList, namingAttributesIterator);

		List<String> arrayList = new ArrayList<String>();

		for (int i = 0; i < namingAttributesList.value.length; i++) {
			for (int j = 0; j < namingAttributesList.value[i].length; j++) {
				if (namingAttributesList.value[i][j].name.equals(CorbaConstants.MULTILAYER_SUBNETWORK_STR)) {
					arrayList.add(namingAttributesList.value[i][j].value);

					if (LOG.isInfoEnabled()) {
						LOG.info("Subnetwork Name: " + namingAttributesList.value[i][j].value);
					}
				}
			}
		}

		boolean exitwhile = false;
		if (namingAttributesIterator.value != null) {
			try {
				boolean hasMoreData = true;
				while (hasMoreData) {
					hasMoreData = namingAttributesIterator.value.next_n(HOW_MANY, namingAttributesList);
					for (int i = 0; i < namingAttributesList.value.length; i++) {
						for (int j = 0; j < namingAttributesList.value[i].length; j++) {
							if (namingAttributesList.value[i][j].name
									.equals(CorbaConstants.MULTILAYER_SUBNETWORK_STR)) {
								arrayList.add(namingAttributesList.value[i][j].value);

								if (LOG.isInfoEnabled()) {
									LOG.info("Subnetwork Name: " + namingAttributesList.value[i][j].value);
								}
							}
						}
					}
				}
				exitwhile = true;
			} finally {
				if (!exitwhile) {
					namingAttributesIterator.value.destroy();
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllTopLevelSubnetworkNames() got {} top level subnetworks.", arrayList.size());
			LOG.info("getAllTopLevelSubnetworkNames() complete.");
		}

		return arrayList;
	}

	public List<String> getAllSubnetworkConnections() throws ProcessingFailureException, SAXException {
		return getAllSubnetworkConnections("SDH");
	}

	public List<String> getAllSubnetworkConnections(final String mlsn) throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllSubnetworkConnections() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		NameAndStringValue_T[] nameAndStringValueArray = new NameAndStringValue_T[2];

		nameAndStringValueArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		nameAndStringValueArray[1] = new NameAndStringValue_T(CorbaConstants.MULTILAYER_SUBNETWORK_STR, mlsn);

		SubnetworkConnectionList_THolder sncList = new SubnetworkConnectionList_THolder();
		SNCIterator_IHolder sncIterator = new SNCIterator_IHolder();
		short[] rateList = new short[0];

		mlsnManager.getAllSubnetworkConnections(nameAndStringValueArray, rateList, HOW_MANY, sncList, sncIterator);
		sncNames = new ArrayList<String>();
		serverTrailTPList = new ArrayList<NameAndStringValue_T[]>();

		for (int i = 0; i < sncList.value.length; i++) {
			String sncID = handler.getValueByName(sncList.value[i].name, CorbaConstants.SUBNETWORK_CONNECTION_STR);

			// Filter MSTRAIL. The MSTRAILS does not have Cross-connects. Hence,
			// getRoute API give ERROR for these SNCs
			if (sncList.value[i].rate != 25 && sncList.value[i].rate != 26 && sncList.value[i].rate != 27
					&& sncList.value[i].rate != 28) {
				sncNames.add(handler.getValueByName(sncList.value[i].name, CorbaConstants.SUBNETWORK_CONNECTION_STR));

			}

			populateServerTrailTPList(sncList.value[i]);

			handler.printStructure(helper.getSubnetworkConnectionParams(sncList.value[i]));
		}

		boolean exitWhile = false;
		if (sncIterator.value != null) {
			try {
				boolean hasMoreData = true;
				while (hasMoreData) {
					hasMoreData = sncIterator.value.next_n(HOW_MANY, sncList);
					for (int i = 0; i < sncList.value.length; i++) {
						// Filter MSTRAIL. The MSTRAILS does not have
						// Cross-connects. Hence,
						// getRoute API gives ERROR for these SNCs
						if (sncList.value[i].rate != 25 && sncList.value[i].rate != 26 && sncList.value[i].rate != 27
								&& sncList.value[i].rate != 28) {
							sncNames.add(handler.getValueByName(sncList.value[i].name,
									CorbaConstants.SUBNETWORK_CONNECTION_STR));
						}
						populateServerTrailTPList(sncList.value[i]);

						handler.printStructure(helper.getSubnetworkConnectionParams(sncList.value[i]));
					}
				}
				exitWhile = true;
			} finally {
				if (!exitWhile) {
					sncIterator.value.destroy();
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllSubnetworkConnections() complete.");
		}

		return sncNames;
	}

	public void getRoute() throws Exception {
		if (LOG.isInfoEnabled()) {
			LOG.info("getRoute() start.");
		}

		if (sncNames == null) {
			sncNames = getAllSubnetworkConnections();
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		NameAndStringValue_T[] sncNameArray = new NameAndStringValue_T[3];

		sncNameArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		sncNameArray[1] = new NameAndStringValue_T(CorbaConstants.MULTILAYER_SUBNETWORK_STR, "SDH");
		sncNameArray[2] = new NameAndStringValue_T();
		sncNameArray[2].name = CorbaConstants.SUBNETWORK_CONNECTION_STR;

		Route_THolder routeHolder = new Route_THolder();

		for (String sncName : sncNames) {
			sncNameArray[2].value = sncName;

			try {
				mlsnManager.getRoute(sncNameArray, true, routeHolder);

				if (LOG.isInfoEnabled()) {
					LOG.info("getRoute: got " + routeHolder.value.length + " Cross-connects for SNC "
							+ sncNameArray[2].value);
				}

				for (CrossConnect_T crossConnect : routeHolder.value) {
					handler.printStructure(helper.getRouteParams(crossConnect, sncNameArray[2].value));
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getRoute. SNC: " + sncNameArray[2].value);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getRoute() complete.");
		}
	}

	// NOT supported by ALU CORBA NBI
	public void getRouteAndTopologicalLinks() throws Exception {
		if (LOG.isInfoEnabled()) {
			LOG.info("getRouteAndTopologicalLinks() start.");
		}

		if (sncNames == null) {
			sncNames = getAllSubnetworkConnections();
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		NameAndStringValue_T[] sncNameArray = new NameAndStringValue_T[3];

		sncNameArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		sncNameArray[1] = new NameAndStringValue_T(CorbaConstants.MULTILAYER_SUBNETWORK_STR, "SDH");
		sncNameArray[2] = new NameAndStringValue_T();
		sncNameArray[2].name = CorbaConstants.SUBNETWORK_CONNECTION_STR;

		Route_THolder routeHolder = new Route_THolder();
		TopologicalLinkList_THolder topologicalLinkList = new TopologicalLinkList_THolder();

		for (String sncName : sncNames) {
			sncNameArray[2].value = sncName;

			try {
				mlsnManager.getRouteAndTopologicalLinks(sncNameArray, routeHolder, topologicalLinkList);

				if (LOG.isInfoEnabled()) {
					LOG.info("getRouteAndTopologicalLinks: got " + routeHolder.value.length + " Cross-connects for SNC "
							+ sncNameArray[2].value);
				}

				for (CrossConnect_T crossConnect : routeHolder.value) {
					handler.printStructure(helper.getRouteParams(crossConnect, sncNameArray[2].value));
				}

				for (int i = 0; i < topologicalLinkList.value.length; i++) {
					handler.printStructure(helper.getRouteAndTopologicalLinksParams(topologicalLinkList.value[i],
							sncNameArray[2].value));
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getRouteAndTopologicalLinks. SNC: " + sncNameArray[2].value);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getRouteAndTopologicalLinks() complete.");
		}
	}

	public void getAllProtectionGroups() throws ProcessingFailureException, SAXException {

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllProtectionGroups() start.");
		}

		if (neNamesWithoutVNE == null) {
			getAllManagedElements();
		}

		if (!setManagerByName(PRT_MANAGER_NAME))
			return;

		NameAndStringValue_T[] nameAndStringValueArray = new NameAndStringValue_T[2];

		nameAndStringValueArray[0] = new NameAndStringValue_T();
		nameAndStringValueArray[0].name = CorbaConstants.EMS_STR;
		nameAndStringValueArray[0].value = emsName;
		nameAndStringValueArray[1] = new NameAndStringValue_T();
		nameAndStringValueArray[1].name = CorbaConstants.MANAGED_ELEMENT_STR;

		ProtectionGroupList_THolder protectionGroupList = new ProtectionGroupList_THolder();
		ProtectionGroupIterator_IHolder protectionGroupIterator = new ProtectionGroupIterator_IHolder();

		int counter = 0;
		boolean exitwhile = false;
		for (String n : neNamesWithoutVNE) {
			try {
				nameAndStringValueArray[1].value = n;
				protectionMgr.getAllProtectionGroups(nameAndStringValueArray, HOW_MANY, protectionGroupList,
						protectionGroupIterator);

				if (LOG.isDebugEnabled()) {
					LOG.debug("getAllProtectionGroups: got " + protectionGroupList.value.length
							+ " pieces of ProtectionGroup for ME " + nameAndStringValueArray[1].value);
				}

				helper.printProtectionGroup(protectionGroupList.value);

				exitwhile = false;
				if (protectionGroupIterator.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = protectionGroupIterator.value.next_n(HOW_MANY, protectionGroupList);

							if (LOG.isDebugEnabled()) {
								LOG.debug("getAllProtectionGroups: got next " + protectionGroupList.value.length
										+ " pieces of ProtectionGroup for ME " + nameAndStringValueArray[1].value);
							}

							helper.printProtectionGroup(protectionGroupList.value);
						}
						exitwhile = true;
					} finally {
						if (!exitwhile) {
							protectionGroupIterator.value.destroy();
						}
					}

					counter++;

					if (LOG.isDebugEnabled()) {
						LOG.debug("getAllProtectionGroups: finished getProtectionGroup for ME "
								+ nameAndStringValueArray[1].value + " Order number # " + counter);
					}
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getAllProtectionGroups. ME: " + n);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllProtectionGroups() complete");
		}
	}

	public void getAllFDFrs() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllFDFrs() start.");
		}

		if (!setManagerByName(FLOW_DOMAIN_MANAGER))
			return;

		NameAndStringValue_T[] fdname = new NameAndStringValue_T[2];
		fdname[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		fdname[1] = new NameAndStringValue_T("FlowDomain", "ETS_0");

		FDFrList_THolder listHolder = new FDFrList_THolder();
		FDFrIterator_IHolder iteratorHolder = new FDFrIterator_IHolder();

		FDFrIterator_I iterator = null;
		short[] rateList = new short[0];

		flowDomainMgr.getAllFDFrs(fdname, HOW_MANY, rateList, listHolder, iteratorHolder);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Got " + (listHolder.value != null ? listHolder.value.length : "0") + " FDFrs");
		}

		helper.printFDFrs(listHolder.value);

		iterator = iteratorHolder.value;
		if (iterator != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Getting more data...");
			}

			boolean hasMoreData = true;
			boolean exitedWhile = false;

			try {
				while (hasMoreData) {
					hasMoreData = iterator.next_n(HOW_MANY, listHolder);
					if (LOG.isDebugEnabled()) {
						LOG.debug("Got " + (listHolder.value != null ? listHolder.value.length : "0") + " FDFrs");
					}

					helper.printFDFrs(listHolder.value);
				}

				exitedWhile = true;
			} finally {
				if (!exitedWhile) {
					iterator.destroy();
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllFDFrs() end.");
		}
	}

	public void getTopologicalLinksOfFDFr() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getTopologicalLinksOfFDFr() start.");
		}

		if (!setManagerByName(EXTEND_SERVICE_MANAGER))
			return;

		NameAndStringValue_T[] fdname = new NameAndStringValue_T[2];
		fdname[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		fdname[1] = new NameAndStringValue_T("FlowDomain", "ETS_0");

		FDFrList_THolder listHolder = new FDFrList_THolder();
		FDFrIterator_IHolder iteratorHolder = new FDFrIterator_IHolder();

		short[] rateList = new short[0];

		flowDomainMgr.getAllFDFrs(fdname, HOW_MANY, rateList, listHolder, iteratorHolder);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Got " + (listHolder.value != null ? listHolder.value.length : "0") + " FDFrs.");
		}

		TopologicalLinkList_THolder topologicalLinkList = new TopologicalLinkList_THolder();
		TopologicalLinkIterator_IHolder topologicalLinkItr = new TopologicalLinkIterator_IHolder();

		boolean exitWhile = false;

		for (FlowDomainFragment_T fdfr : listHolder.value) {
			try {
				extendServiceMgr.getTopologicalLinksOfFDFr(fdfr.name, HOW_MANY, topologicalLinkList,
						topologicalLinkItr);

				if (LOG.isDebugEnabled()) {
					LOG.debug("Got " + (topologicalLinkList.value != null ? topologicalLinkList.value.length : "0")
							+ " Transport link.");
				}

				helper.printTopologicalLinksOfFDFr(fdfr, topologicalLinkList.value);

				exitWhile = false;

				if (topologicalLinkItr.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = topologicalLinkItr.value.next_n(HOW_MANY, topologicalLinkList);

							helper.printTopologicalLinksOfFDFr(fdfr, topologicalLinkList.value);
						}

						exitWhile = true;
					} finally {
						if (!exitWhile)
							topologicalLinkItr.value.destroy();
					}
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex,
						"getTopologicalLinksOfFDFr. FDFR: " + handler.convertNameAndStringValueToString(fdfr.name));
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getTopologicalLinksOfFDFr() complete.");
		}
	}

	public StringHolder createAndActivateSNC(SNCCreateData_T createData, GradesOfImpact_T tolerableImpact,
			EMSFreedomLevel_T emsFreedomLevel, TPDataList_THolder tpsToModify) throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("createAndActivateSNC() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		SubnetworkConnection_THolder theSNC = new SubnetworkConnection_THolder();
		StringHolder errorReason = new StringHolder();

		this.mlsnManager.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify, theSNC,
				errorReason);

		if (LOG.isInfoEnabled()) {
			LOG.info("createAndActivateSNC() complete.");
		}

		return errorReason;
	}

	public StringHolder deactivateAndDeleteSNC(NameAndStringValue_T[] sncName, GradesOfImpact_T tolerableImpact,
			EMSFreedomLevel_T emsFreedomLevel, TPDataList_THolder tpsToModify) throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("deactivateAndDeleteSNC() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		SubnetworkConnection_THolder theSNC = new SubnetworkConnection_THolder();
		StringHolder errorReason = new StringHolder();

		this.mlsnManager.deactivateAndDeleteSNC(sncName, tolerableImpact, emsFreedomLevel, tpsToModify, theSNC,
				errorReason);

		if (LOG.isInfoEnabled()) {
			LOG.info(" deactivateAndDeleteSNC() complete.");
		}

		return errorReason;
	}

	public StringHolder modifySNC(NameAndStringValue_T[] sncName, SNCModifyData_T modifyData,
			GradesOfImpact_T tolerableImpact, ProtectionEffort_T tolerableImpactEffort,
			EMSFreedomLevel_T emsFreedomLevel, TPDataList_THolder tpsToModify) throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("modifySNC() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		SubnetworkConnection_THolder theSNC = new SubnetworkConnection_THolder();
		StringHolder errorReason = new StringHolder();

		this.mlsnManager.modifySNC(sncName, "", modifyData, tolerableImpact, tolerableImpactEffort, emsFreedomLevel,
				tpsToModify, theSNC, errorReason);

		if (LOG.isInfoEnabled()) {
			LOG.info("modifySNC() complete.");
		}

		return errorReason;
	}

	public void getSNCsByUserLabel(String userLabel) throws ProcessingFailureException, SAXException {

		if (LOG.isInfoEnabled()) {
			LOG.info("getSNCsByUserLabel() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		SubnetworkConnectionList_THolder sncList = new SubnetworkConnectionList_THolder();

		mlsnManager.getSNCsByUserLabel(userLabel, sncList);

		for (int i = 0; i < sncList.value.length; i++) {

			handler.printStructure(helper.getSubnetworkConnectionParams(sncList.value[i]));
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getSNCsByUserLabel() complete.");
		}

	}

	public void getContainedPotentialTPs() throws ProcessingFailureException, SAXException {

		if (LOG.isInfoEnabled()) {
			LOG.info("getContainedPotentialTPs() start.");
		}

		if (!setManagerByName(ME_MANAGER_NAME))
			return;

		if (this.serverTrailTPList == null) {
			getAllSubnetworkConnections();
		}

		short[] tpLayerRateList = new short[0];

		TerminationPointList_THolder terminationPointList = new TerminationPointList_THolder();
		TerminationPointIterator_IHolder terminationPointIterator = new TerminationPointIterator_IHolder();

		int counter = 0;
		boolean exitWhile = false;
		for (int count = 0; count < this.serverTrailTPList.size(); count++) {

			NameAndStringValue_T[] inputNE = new NameAndStringValue_T[4];

			try {

				NameAndStringValue_T[] eachNameAndStringValueArray = this.serverTrailTPList.get(count);

				inputNE[0] = new NameAndStringValue_T(eachNameAndStringValueArray[0].name,
						eachNameAndStringValueArray[0].value);

				inputNE[1] = new NameAndStringValue_T(eachNameAndStringValueArray[1].name,
						eachNameAndStringValueArray[1].value);

				inputNE[2] = new NameAndStringValue_T(eachNameAndStringValueArray[2].name,
						eachNameAndStringValueArray[2].value);

				inputNE[3] = new NameAndStringValue_T(eachNameAndStringValueArray[3].name,
						eachNameAndStringValueArray[3].value);

				String sncID = eachNameAndStringValueArray[4].value;

				LOG.info("SNC_ID:" + sncID);
				LOG.info("NEID:" + eachNameAndStringValueArray[1].value);
				LOG.info("PTP:" + eachNameAndStringValueArray[2].value);
				LOG.info("CTP:" + eachNameAndStringValueArray[3].value);
				LOG.info("*****************************************");

				meManager.getContainedPotentialTPs(inputNE, tpLayerRateList, HOW_MANY, terminationPointList,
						terminationPointIterator);

				for (int i = 0; i < terminationPointList.value.length; i++) {
					helper.printContainedTPParams(terminationPointList.value[i], sncID, inputNE[3].value);
				}

				exitWhile = false;

				if (terminationPointIterator.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = terminationPointIterator.value.next_n(HOW_MANY, terminationPointList);

							for (int i = 0; i < terminationPointList.value.length; i++) {
								helper.printContainedTPParams(terminationPointList.value[i], sncID, inputNE[3].value);
							}
						}
						exitWhile = true;
					} finally {
						if (!exitWhile) {
							terminationPointIterator.value.destroy();
						}
					}

					counter++;

				}

			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getContainedPotentialTPs. ME: " + inputNE);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getContainedPotentialTPs() complete.");
		}

	}

	private void populateServerTrailTPList(SubnetworkConnection_T snc) {

		String sncID = handler.getValueByName(snc.name, CorbaConstants.SUBNETWORK_CONNECTION_STR);

		int rate = snc.rate;

		if (rate == 15 && sncID != null && sncID.startsWith("TRAIL")) {

			String aEndPTP = handler.getValueByName(snc.aEnd[0].tpName, CorbaConstants.PTP_STR);

			if (aEndPTP != null && !aEndPTP.endsWith("gMAU")) {

				NameAndStringValue_T[] serverTrailAEndTPNameArray = new NameAndStringValue_T[5];

				serverTrailAEndTPNameArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);

				serverTrailAEndTPNameArray[1] = new NameAndStringValue_T(CorbaConstants.MANAGED_ELEMENT_STR,
						handler.getValueByName(snc.aEnd[0].tpName, CorbaConstants.MANAGED_ELEMENT_STR));

				serverTrailAEndTPNameArray[2] = new NameAndStringValue_T(CorbaConstants.PTP_STR, aEndPTP);

				serverTrailAEndTPNameArray[3] = new NameAndStringValue_T(CorbaConstants.CTP_STR,
						handler.getValueByName(snc.aEnd[0].tpName, CorbaConstants.CTP_STR));

				serverTrailAEndTPNameArray[4] = new NameAndStringValue_T(CorbaConstants.SNC_ID_STR, sncID);

				serverTrailTPList.add(serverTrailAEndTPNameArray);
			}

			String zEndPTP = handler.getValueByName(snc.zEnd[0].tpName, CorbaConstants.PTP_STR);

			if (zEndPTP != null && !zEndPTP.endsWith("gMAU")) {

				NameAndStringValue_T[] serverTrailZEndTPNameArray = new NameAndStringValue_T[5];

				serverTrailZEndTPNameArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);

				serverTrailZEndTPNameArray[1] = new NameAndStringValue_T(CorbaConstants.MANAGED_ELEMENT_STR,
						handler.getValueByName(snc.zEnd[0].tpName, CorbaConstants.MANAGED_ELEMENT_STR));

				serverTrailZEndTPNameArray[2] = new NameAndStringValue_T(CorbaConstants.PTP_STR, zEndPTP);

				serverTrailZEndTPNameArray[3] = new NameAndStringValue_T(CorbaConstants.CTP_STR,
						handler.getValueByName(snc.zEnd[0].tpName, CorbaConstants.CTP_STR));

				serverTrailZEndTPNameArray[4] = new NameAndStringValue_T(CorbaConstants.SNC_ID_STR, sncID);

				serverTrailTPList.add(serverTrailZEndTPNameArray);
			}
		}
	}

	public void handleProcessingFailureException(ProcessingFailureException pfe, String param)
			throws ProcessingFailureException {
		CorbaErrorProcessor err = new CorbaErrorProcessor(pfe);

		if (err.getPriority() == CorbaErrorDescriptions.PRIORITY.MAJOR) {
			LOG.error("Alcatel OMS 1350>> " + param + ", " + err.printError()
					+ " It is a major error. Stop interaction with server", pfe);

			throw pfe;
		} else {
			LOG.error("Alcatel OMS 1350>> " + param + err.printError()
					+ " It is a minor error. Continue interaction with server", pfe);
		}
	}
}
