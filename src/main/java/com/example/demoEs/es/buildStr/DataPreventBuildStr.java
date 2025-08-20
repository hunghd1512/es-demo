package com.example.demoEs.es.buildStr;

import com.example.demoEs.es.document.OrganizationDataEntryES;
import com.example.demoEs.es.document.PersonalDataEntryES;

public class DataPreventBuildStr {
    public static String buildOrgStr(OrganizationDataEntryES organizationDataEntryES){
        return organizationDataEntryES.toString();
    }

    public static String buildPersonStr(PersonalDataEntryES personalDataEntryES){
        return personalDataEntryES.toString();
    }
}
