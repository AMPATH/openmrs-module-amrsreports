package org.openmrs.module.amrsreports.web.controller;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Settings page for default values in AMRS Reports
 */
@Controller
@RequestMapping("module/amrsreports/settings.form")
public class AmrsReportsSettingsController {

	@ModelAttribute("identifierTypes")
	public List<PatientIdentifierType> getAllIdentifierTypes() {
		return Context.getPatientService().getAllPatientIdentifierTypes();
	}

	@ModelAttribute("cccIdentifierType")
	public String getCCCIdentifierType() {
		return Context.getAdministrationService()
				.getGlobalProperty(AmrsReportsConstants.GP_CCC_NUMBER_IDENTIFIER_TYPE);
	}

    @ModelAttribute("attributeTypes")
    public List<PersonAttributeType> getAllPersonAttributeTypes() {
        return Context.getPersonService().getAllPersonAttributeTypes();
    }

    @ModelAttribute("tbRegistrationAttributeType")
    public String getTbRegistrationNoAttributeType() {
        return Context.getAdministrationService()
                .getGlobalProperty(AmrsReportsConstants.TB_REGISTRATION_NO_ATTRIBUTE_TYPE);
    }

	@ModelAttribute("productionServerURL")
	public String getProductionServerURL() {
		return Context.getAdministrationService()
				.getGlobalProperty(AmrsReportsConstants.GP_PRODUCTION_SERVER_URL);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String viewSettings() {
		return "module/amrsreports/settings";
	}

}
