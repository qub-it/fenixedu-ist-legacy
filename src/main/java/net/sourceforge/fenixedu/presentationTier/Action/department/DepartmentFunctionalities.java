/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Core.
 *
 * FenixEdu Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.ui.struts.action.department;

import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.academic.domain.organizationalStructure.ScientificAreaUnit;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.ui.struts.action.commons.UnitFunctionalities;
import org.fenixedu.academic.ui.struts.action.departmentAdmOffice.DepartmentAdmOfficeApp.DepartmentAdmOfficeMessagingApp;

import org.abego.treelayout.internal.util.Contract;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

@StrutsFunctionality(app = DepartmentAdmOfficeMessagingApp.class, path = "manage-files", titleKey = "label.manageFiles")
@Mapping(module = "departmentAdmOffice", path = "/departmentFunctionalities")
@Forwards({ @Forward(name = "uploadFile", path = "/commons/unitFiles/uploadFile.jsp"),
        @Forward(name = "manageFiles", path = "/commons/unitFiles/manageFiles.jsp"),
        @Forward(name = "editUploaders", path = "/commons/PersistentMemberGroups/configureUploaders.jsp"),
        @Forward(name = "managePersistedGroups", path = "/commons/PersistentMemberGroups/managePersistedGroups.jsp"),
        @Forward(name = "editFile", path = "/commons/unitFiles/editFile.jsp"),
        @Forward(name = "editPersistedGroup", path = "/commons/PersistentMemberGroups/editPersistedGroup.jsp"),
        @Forward(name = "createPersistedGroup", path = "/commons/PersistentMemberGroups/createPersistedGroup.jsp"),
        @Forward(name = "chooseUnit", path = "/departmentMember/chooseUnit.jsp") })
public class DepartmentFunctionalities extends UnitFunctionalities {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setAttribute("functionalityAction", "departmentFunctionalities");
        request.setAttribute("module", mapping.getModuleConfig().getPrefix().substring(1));
        return super.execute(mapping, form, request, response);
    }

    @EntryPoint
    public ActionForward chooseUnit(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final Unit unit = getUnit(request);
        if (unit != null) {
            return manageFiles(mapping, actionForm, request, response);
        }
        Unit departmentUnit = AccessControl.getPerson().getEmployee().getCurrentWorkingPlace().getDepartmentUnit();
        Set<Unit> units = new TreeSet<>(Party.COMPARATOR_BY_NAME);
        units.add(departmentUnit);

        for (Unit subUnit : departmentUnit.getAllSubUnits()) {
            if (subUnit.isScientificAreaUnit()) {
                ScientificAreaUnit scientificAreaUnit = (ScientificAreaUnit) subUnit;
                if (isCurrentUserMemberOfScientificArea(scientificAreaUnit)) {
                    units.add(scientificAreaUnit);
                }
            }
        }

        if (units.size() == 1) {
            request.setAttribute("unit", departmentUnit);
            request.setAttribute("unitId", departmentUnit.getExternalId());
            return manageFiles(mapping, actionForm, request, response);
        }

        request.setAttribute("units", units);
        return mapping.findForward("chooseUnit");
    }

    private static boolean isCurrentUserMemberOfScientificArea(ScientificAreaUnit scientificAreaUnit) {
        for (Contract contract : EmployeeContract.getWorkingContracts(scientificAreaUnit)) {
            if (contract.getPerson().equals(AccessControl.getPerson())) {
                return true;
            }
        }
        return false;
    }

}
