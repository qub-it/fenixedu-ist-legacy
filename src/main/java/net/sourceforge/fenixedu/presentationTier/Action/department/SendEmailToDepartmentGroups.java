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

import org.fenixedu.academic.domain.Department;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.academic.domain.organizationalStructure.ScientificAreaUnit;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.util.email.PersonSender;
import org.fenixedu.academic.domain.util.email.Recipient;
import org.fenixedu.academic.domain.util.email.Sender;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.ui.struts.action.departmentMember.DepartmentMemberApp.DepartmentMemberMessagingApp;
import org.fenixedu.academic.ui.struts.action.messaging.EmailsDA;
import org.fenixedu.academic.ui.struts.action.messaging.UnitMailSenderAction;

import org.abego.treelayout.internal.util.Contract;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

@StrutsFunctionality(app = DepartmentMemberMessagingApp.class, path = "send-email-to-department-groups",
        titleKey = "label.sendEmailToGroups")
@Mapping(module = "departmentMember", path = "/sendEmailToDepartmentGroups")
@Forwards(@Forward(name = "chooseUnit", path = "/departmentMember/chooseUnit.jsp"))
public class SendEmailToDepartmentGroups extends UnitMailSenderAction {

    @EntryPoint
    public ActionForward chooseUnit(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        final Unit unit = getUnit(request);
        if (unit != null) {
            return prepare(mapping, actionForm, request, response);
        }
        Set<Unit> units = new TreeSet<>(Party.COMPARATOR_BY_NAME);
        Unit departmentUnit = getDepartment();
        if (departmentUnit != null) {
            units.add(departmentUnit);

            for (Unit subUnit : departmentUnit.getAllSubUnits()) {
                if (subUnit.isScientificAreaUnit()) {
                    ScientificAreaUnit scientificAreaUnit = (ScientificAreaUnit) subUnit;
                    if (isCurrentUserMemberOfScientificArea(scientificAreaUnit)) {
                        units.add(scientificAreaUnit);
                    }
                }
            }
        }

        if (units.size() == 1) {
            request.setAttribute("unitId", departmentUnit.getExternalId());
            return prepare(mapping, actionForm, request, response);
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

    private Unit getDepartment() {
        final Person person = AccessControl.getPerson();
        final Teacher teacher = person.getTeacher();
        if (teacher != null) {
            return teacher.getDepartment().getDepartmentUnit();
        }
        final Employee employee = person.getEmployee();
        if (employee != null) {
            return employee.getCurrentWorkingPlace().getDepartmentUnit();
        }
        return null;
    }

    @Override
    public ActionForward prepare(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        final Unit unit = getUnit(request);

        final Sender unitSender = getSomeSender(unit);

        if (userOfficialSender(unit, unitSender)) {
            return EmailsDA.sendEmail(request, unitSender);
        } else {
            final Person person = AccessControl.getPerson();
            final PersonSender sender = person.getSender();

            return unitSender == null ? EmailsDA.sendEmail(request, sender) : EmailsDA.sendEmail(request, sender, unitSender
                    .getRecipientsSet().toArray(new Recipient[0]));
        }
    }

    private boolean userOfficialSender(final Unit unit, final Sender unitSender) {
        if (unit instanceof DepartmentUnit) {
            final DepartmentUnit departmentUnit = (DepartmentUnit) unit;
            final Department department = departmentUnit.getDepartment();
            return DepartmentPresidentStrategy.isCurrentUserCurrentDepartmentPresident(department) && unitSender != null;
        }
        return false;
    }

    private Sender getSomeSender(final Unit unit) {
        for (final Sender sender : unit.getUnitBasedSenderSet()) {
            return sender;
        }
        return null;
    }

}
