package ex.corba.huawei.transform.sax;

import ex.corba.CorbaConstants;


/**
 * Describe structure of XML that is created as result of serialization of CORBA entities 
 */
public enum Corba2XMLStructure {
    /**
     * CORBA entity: ManagedElement_T
     */
    NETWORK_ELEMENTS("NETWORK_ELEMENTS", new String[]{
            CorbaConstants.NE_ID_STR,
            CorbaConstants.USER_LABEL_STR,
            CorbaConstants.NE_NAME_STR,
            CorbaConstants.OWNER_STR,
            CorbaConstants.LOCATION_STR,
            CorbaConstants.VERSION_STR,
            CorbaConstants.PRODUCT_NAME_STR,
            CorbaConstants.COMMUNICATION_STATE_STR,
            CorbaConstants.EMS_INSYNC_STATE_STR,
            CorbaConstants.SUPPORTED_RATES_STR,
            CorbaConstants.GATEWAYS_STR
    }),

    /**
     * CORBA entity: Equipment_T
     */
    CARDS("CARDS", new String[]{
            CorbaConstants.NE_ID_STR,
            CorbaConstants.HOLDER_STR,
            CorbaConstants.SLOT_N_STR,
            CorbaConstants.USER_LABEL_STR,
            CorbaConstants.NATIVE_EMS_NAME_STR,
            CorbaConstants.OWNER_STR,
            CorbaConstants.ALARM_REPORT_INDIC_STR,
            CorbaConstants.SERVICE_STATE_STR,
            CorbaConstants.EXP_EQUIP_OBJ_TYPE_STR,
            CorbaConstants.INST_EQUIP_OBJ_TYPE_STR,
            CorbaConstants.INST_PART_NUMBER_STR,
            CorbaConstants.INST_VERSION_STR,
            CorbaConstants.INST_SERIAL_NUMBER_STR,
            CorbaConstants.ADDITIONAL_INFO_STR
    }),

    /**
     * CORBA entity: EquipmentHolder_T
     */
    HOLDERS("HOLDERS", new String[]{
            CorbaConstants.NE_ID_STR,
            CorbaConstants.HOLDER_STR,
            CorbaConstants.USER_LABEL_STR,
            CorbaConstants.NATIVE_EMS_NAME_STR,
            CorbaConstants.OWNER_STR,
            CorbaConstants.ALARM_REPORT_INDIC_STR,
            CorbaConstants.HOLDER_TYPE_STR,
            CorbaConstants.EXP_INST_EQUIPMENT_STR,
            CorbaConstants.ACCEPT_EQUIPMENT_STR,
            CorbaConstants.STATE_STR,
            CorbaConstants.ADDITIONAL_INFO_STR
    }),
    
    /**
     * CORBA entity: TerminationPoint_T
     */
    PTPS("PTPS", new String[]{
            CorbaConstants.NE_ID_STR,
            CorbaConstants.USER_LABEL_STR,
            CorbaConstants.NATIVE_EMS_NAME_STR,
            CorbaConstants.OWNER_STR,
            CorbaConstants.IN_TRAFFIC_DES_NAME_STR,
            CorbaConstants.EG_TRAFFIC_DES_NAME_STR,
            CorbaConstants.PTP_STR,
            CorbaConstants.FTP_STR,
            CorbaConstants.CTP_STR,
            CorbaConstants.TYPE_STR,
            CorbaConstants.CONNECTION_STATE_STR,
            CorbaConstants.TP_MAPPING_MODE_STR,
            CorbaConstants.DIRECTION_STR,
            CorbaConstants.TRANSMISSION_PARAMS_STR,
            CorbaConstants.TP_PROTECTION_ASSOCIATION_STR,
            CorbaConstants.EDGE_POINT_STR,
            CorbaConstants.ADDITIONAL_INFO_STR
    }),    
    
    /**
     * CORBA entity: TopologicalLink_T
     */
    TOPOL_LINKS("TOPO_LINKS", new String[]{
            CorbaConstants.USER_LABEL_STR,
            CorbaConstants.TL_ID_STR,
            CorbaConstants.NATIVE_EMS_NAME_STR,
            CorbaConstants.OWNER_STR,
            CorbaConstants.DIRECTION_STR,
            CorbaConstants.RATE_STR,
            CorbaConstants.A_END_NE_STR,
            CorbaConstants.A_END_TP_STR,
            CorbaConstants.Z_END_NE_STR,
            CorbaConstants.Z_END_TP_STR,
            CorbaConstants.A_TRANSMISSION_PARAMS_STR,
            CorbaConstants.Z_TRANSMISSION_PARAMS_STR
    }),

    /**
     * CORBA entity: SubnetworkConnection_T
     */
    SNCS("SNCS", new String[]{
            CorbaConstants.SNC_ID_STR,
            CorbaConstants.USER_LABEL_STR,
            CorbaConstants.NATIVE_EMS_NAME_STR,
            CorbaConstants.OWNER_STR,
            CorbaConstants.SNC_STATE_STR,
            CorbaConstants.DIRECTION_STR,
            CorbaConstants.RATE_STR,
            CorbaConstants.STATIC_PROTECTION_LEVEL_STR,
            CorbaConstants.SNC_TYPE_STR,
            CorbaConstants.A1_TPNAME_NE_STR,
            CorbaConstants.A1_TPNAME_PTP_STR,
            CorbaConstants.A1_TPNAME_CTP_STR,
            CorbaConstants.A1_TPMAPPING_MODE_STR,
            CorbaConstants.A2_TPNAME_NE_STR,
            CorbaConstants.A2_TPNAME_PTP_STR,
            CorbaConstants.A2_TPNAME_CTP_STR,
            CorbaConstants.A2_TPMAPPING_MODE_STR,
            CorbaConstants.Z1_TPNAME_NE_STR,
            CorbaConstants.Z1_TPNAME_PTP_STR,
            CorbaConstants.Z1_TPNAME_CTP_STR,
            CorbaConstants.Z1_TPMAPPING_MODE_STR,
            CorbaConstants.Z2_TPNAME_NE_STR,
            CorbaConstants.Z2_TPNAME_PTP_STR,
            CorbaConstants.Z2_TPNAME_CTP_STR,
            CorbaConstants.Z2_TPMAPPING_MODE_STR,
            CorbaConstants.REROUTE_ALLOWED_STR,
            CorbaConstants.NETWORK_REROUTED_STR,
            CorbaConstants.FREQ_A_STR,
            CorbaConstants.FREQ_Z_STR,
            CorbaConstants.ADDITIONAL_INFO_STR
    }),

    /**
     * CORBA entity: CrossConnect_T
     */
    ROUTES("ROUTES", new String[]{
            CorbaConstants.SNC_ID_STR,
            CorbaConstants.ACTIVE_STR,
            CorbaConstants.DIRECTION_STR,
            CorbaConstants.CC_TYPE_STR,
            CorbaConstants.AI_DIRECTION_STR,
            CorbaConstants.PRT_ROLE_STR,
            CorbaConstants.A1_NE_STR,
            CorbaConstants.A1_PTP_STR,
            CorbaConstants.A1_CTP_STR,
            CorbaConstants.Z1_NE_STR,
            CorbaConstants.Z1_PTP_STR,
            CorbaConstants.Z1_CTP_STR,
            CorbaConstants.A2_NE_STR,
            CorbaConstants.A2_PTP_STR,
            CorbaConstants.A2_CTP_STR,
            CorbaConstants.Z2_NE_STR,
            CorbaConstants.Z2_PTP_STR,
            CorbaConstants.Z2_CTP_STR,
    }),

    /**
     * CORBA entity: ProtectionGroup_T
     */
    PROTECTION_GROUPS("PROTECTION_GROUPS", new String[]{
            CorbaConstants.NE_ID_STR,
            CorbaConstants.USER_LABEL_STR,
            CorbaConstants.NATIVE_EMS_NAME_STR,
            CorbaConstants.OWNER_STR,
            CorbaConstants.PROTECTION_GROUP_TYPE_STR,
            CorbaConstants.PROTECTION_SCHEMA_STATE_STR,
            CorbaConstants.REVERSION_MODE_STR,
            CorbaConstants.RATE_STR,
            CorbaConstants.PGP_TP_LIST_STR,
            CorbaConstants.PGP_PARAMETERS_STR,
            CorbaConstants.ADDITIONAL_INFO_STR
    }),
    
    /**
     * CORBA entity: HW_MSTPEndPoint_T - Huawei specific type
     */
    ENDPOINTS("ENDPOINT", new String[]{
            CorbaConstants.NATIVE_EMS_NAME_STR,
            CorbaConstants.USER_LABEL_STR,
            CorbaConstants.OWNER_STR,
            CorbaConstants.NAME_STR,
            CorbaConstants.TYPE_STR,
            CorbaConstants.TRANSMISSION_PARAMS_STR,
            CorbaConstants.ADDITIONAL_INFO_STR            
    }),
    
    /**
     * CORBA entity: HW_RPRNode_T - Huawei specific type
     */
    ETHSERVICES("ETHSERVICE", new String[]{
            CorbaConstants.NATIVE_EMS_NAME_STR,
            CorbaConstants.USER_LABEL_STR,
            CorbaConstants.OWNER_STR,
            CorbaConstants.NAME_STR,
            CorbaConstants.DIRECTION_STR,
            CorbaConstants.ACTIVE_STATE_STR,
            CorbaConstants.SERVICE_TYPE_STR,
            CorbaConstants.A_END_NE_STR,
            CorbaConstants.A_END_PTP_STR,
            CorbaConstants.Z_END_NE_STR,
            CorbaConstants.Z_END_PTP_STR,
            CorbaConstants.A_END_VLAN_STR,
            CorbaConstants.Z_END_VLAN_STR,
            CorbaConstants.A_END_VC_STR,
            CorbaConstants.Z_END_VC_STR,
            CorbaConstants.A_END_TUNEL_STR,
            CorbaConstants.Z_END_TUNEL_STR,
            CorbaConstants.ADDITIONAL_INFO_STR            
    }),

    /**
     * CORBA entity: HW_MSTPBindingPath_T
     */
    BINDING_PATH("BINDING_PATH", new String[]{
    		CorbaConstants.VCTRUNK_NE_STR,
    		CorbaConstants.VCTRUNK_PTP_STR,
            CorbaConstants.DIRECTION_STR,
            CorbaConstants.ALL_PATH_LIST_STR,
            CorbaConstants.USED_PATH_LIST_STR,
            CorbaConstants.ADDITIONAL_INFO_STR
    });

    private String table;
    private String[] fields;

    private Corba2XMLStructure(String table, String[] fields)  {
        this.table = table;
        this.fields = fields;
    }

    public String getName() {
        return table;
    }

    public String[] getFields() {
        return fields;
    }

    public int getFieldsCount() {
        return fields.length;
    }

    public String getFieldName(int index) {
        return index < fields.length ? fields[index] : "";
    }

    public static Corba2XMLStructure fromName(String v) {
        for (Corba2XMLStructure c : Corba2XMLStructure.values()) {
            if (c.getName().equals(v)) {
                return c;
            }
        }
        
        throw new IllegalArgumentException(String.valueOf(v));
    }

    public boolean isFieldExists(String field) {
        for (String s:getFields()){
            if (s.equals(field)){
               return true;
            }
        }
        
        return false;
    }

    @Override
    public String toString() {
        return table;
    }
}

