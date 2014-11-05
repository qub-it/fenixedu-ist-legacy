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
package org.fenixedu.academic.ui.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fenixedu.academic.domain.Department;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accessControl.DepartmentPresidentStrategy;
import org.fenixedu.academic.domain.alumni.CerimonyInquiryPerson;
import org.fenixedu.academic.domain.inquiries.DelegateInquiryTemplate;
import org.fenixedu.academic.domain.inquiries.InquiryStudentCycleAnswer;
import org.fenixedu.academic.domain.inquiries.RegentInquiryTemplate;
import org.fenixedu.academic.domain.inquiries.StudentInquiryRegistry;
import org.fenixedu.academic.domain.inquiries.TeacherInquiryTemplate;
import org.fenixedu.academic.domain.person.RoleType;
import org.fenixedu.academic.domain.personnelSection.contracts.PersonProfessionalData;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.teacher.ReductionService;
import org.fenixedu.academic.domain.teacher.TeacherService;
import org.fenixedu.academic.domain.time.calendarStructure.TeacherCreditsFillingCE;
import org.fenixedu.academic.ui.struts.action.base.FenixAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.filters.CasAuthenticationFilter;
import org.fenixedu.bennu.core.security.Authenticate;

import pt.ist.fenixWebFramework.renderers.components.HtmlLink;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

public abstract class BaseAuthenticationAction extends FenixAction {

    @Override
    public final ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {

            if (!Authenticate.isLogged() && request.getAttribute(CasAuthenticationFilter.AUTHENTICATION_EXCEPTION_KEY) == null) {
                response.sendRedirect(request.getContextPath() + "/login?callback=" + request.getRequestURL().toString());
                return null;
            }

            final User userView = Authenticate.getUser();

            if (userView == null || userView.isLoginExpired()) {
                return getAuthenticationFailedForward(mapping, request, "errors.noAuthorization", "errors.noAuthorization");
            }

            final HttpSession httpSession = request.getSession(false);

            if (hasMissingTeacherService(userView)) {
                return handleSessionCreationAndForwardToTeachingService(request, userView, httpSession);
            } else if (hasPendingTeachingReductionService(userView)) {
                return handleSessionCreationAndForwardToPendingTeachingReductionService(request, userView, httpSession);
            } else if (hasMissingRAIDESInformation(userView)) {
                return handleSessionCreationAndForwardToRAIDESInquiriesResponseQuestion(request, userView, httpSession);
            } else if (isAlumniAndHasInquiriesToResponde(userView)) {
                return handleSessionCreationAndForwardToAlumniInquiriesResponseQuestion(request, userView, httpSession);
            } else if (isStudentAndHasQucInquiriesToRespond(userView)) {
                return handleSessionCreationAndForwardToQucInquiriesResponseQuestion(request, userView, httpSession);
            } else if (isDelegateAndHasInquiriesToRespond(userView)) {
                return handleSessionCreationAndForwardToDelegateInquiriesResponseQuestion(request, userView, httpSession);
            } else if (isTeacherAndHasInquiriesToRespond(userView)) {
                return handleSessionCreationAndForwardToTeachingInquiriesResponseQuestion(request, userView, httpSession);
            } else if (isRegentAndHasInquiriesToRespond(userView)) {
                return handleSessionCreationAndForwardToRegentInquiriesResponseQuestion(request, userView, httpSession);
            } else if (isStudentAndHasFirstTimeCycleInquiryToRespond(userView)) {
                return handleSessionCreationAndForwardToFirstTimeCycleInquiry(request, userView, httpSession);
            } else if (isStudentAndHasGratuityDebtsToPay(userView)) {
                return handleSessionCreationAndForwardToGratuityPaymentsReminder(request, userView, httpSession);
            } else if (isAlumniWithNoData(userView)) {
                return handleSessionCreationAndForwardToAlumniReminder(request, userView, httpSession);
            } else if (hasPendingPartyContactValidationRequests(userView)) {
                return handlePartyContactValidationRequests(request, userView, httpSession);
            } else {
                return handleSessionCreationAndGetForward(mapping, request, userView, httpSession);
            }
        } catch (AuthorizationException e) {
            return getAuthenticationFailedForward(mapping, request, "invalidAuthentication", "errors.invalidAuthentication");
        }
    }

    private ActionForward handleSessionCreationAndForwardToFirstTimeCycleInquiry(HttpServletRequest request, User userView,
            HttpSession session) {
        return new ActionForward("/respondToFirstTimeCycleInquiry.do?method=showQuestion");
    }

    private boolean isStudentAndHasFirstTimeCycleInquiryToRespond(User userView) {
        if (userView.getPerson().hasRole(RoleType.STUDENT)) {
            final Student student = userView.getPerson().getStudent();
            return student != null && InquiryStudentCycleAnswer.hasFirstTimeCycleInquiryToRespond(student);
        }
        return false;
    }

    private boolean hasMissingTeacherService(User userView) {
        if (userView.getPerson() != null && userView.getPerson().getTeacher() != null
                && userView.getPerson().hasRole(RoleType.DEPARTMENT_MEMBER)) {
            ExecutionSemester executionSemester = ExecutionSemester.readActualExecutionSemester();
            if (executionSemester != null
                    && (PersonProfessionalData.isTeacherActiveForSemester(userView.getPerson().getTeacher(), executionSemester) || userView
                            .getPerson().getTeacher().hasTeacherAuthorization())) {
                TeacherService teacherService =
                        TeacherService.getTeacherServiceByExecutionPeriod(userView.getPerson().getTeacher(), executionSemester);
                return (teacherService == null || teacherService.getTeacherServiceLock() == null)
                        && TeacherCreditsFillingCE.isInValidCreditsPeriod(executionSemester, RoleType.DEPARTMENT_MEMBER);
            }
        }
        return false;
    }

    private boolean hasPendingTeachingReductionService(User userView) {
        if (userView.getPerson() != null && userView.getPerson().getTeacher() != null
                && userView.getPerson().hasRole(RoleType.DEPARTMENT_MEMBER)) {
            Department department = userView.getPerson().getTeacher().getDepartment();
            if (department != null && DepartmentPresidentStrategy.isCurrentUserCurrentDepartmentPresident(department)) {
                ExecutionSemester executionSemester = ExecutionSemester.readActualExecutionSemester();
                if (executionSemester != null
                        && TeacherCreditsFillingCE.isInValidCreditsPeriod(executionSemester,
                                RoleType.DEPARTMENT_ADMINISTRATIVE_OFFICE)) {
                    boolean inValidTeacherCreditsPeriod =
                            TeacherCreditsFillingCE.isInValidCreditsPeriod(executionSemester, RoleType.DEPARTMENT_MEMBER);
                    for (ReductionService reductionService : department.getPendingReductionServicesSet()) {
                        if ((reductionService.getTeacherService().getTeacherServiceLock() != null || !inValidTeacherCreditsPeriod)
                                && reductionService.getTeacherService().getExecutionPeriod().equals(executionSemester)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private ActionForward handlePartyContactValidationRequests(HttpServletRequest request, User userView, HttpSession session) {
        return new ActionForward("/partyContactValidationReminder.do?method=showReminder");
    }

    private boolean hasMissingRAIDESInformation(User userView) {
        return userView.getPerson() != null && userView.getPerson().getStudent() != null
                && userView.getPerson().getStudent().hasAnyMissingPersonalInformation();
    }

    private boolean hasPendingPartyContactValidationRequests(User userView) {
        final Person person = userView.getPerson();
        return person.hasPendingPartyContacts() && person.getCanValidateContacts();
    }

    private boolean isAlumniAndHasInquiriesToResponde(final User userView) {
        for (final CerimonyInquiryPerson cerimonyInquiryPerson : userView.getPerson().getCerimonyInquiryPersonSet()) {
            if (cerimonyInquiryPerson.isPendingResponse()) {
                return true;
            }
        }
        return false;
    }

    private ActionForward handleSessionCreationAndForwardToAlumniReminder(HttpServletRequest request, User userView,
            HttpSession session) {
        return new ActionForward("/alumniReminder.do");
    }

    /**
     * Checks if all the person that have the Alumni object have the any
     * formation filled in with the exception for those that are active teachers
     * or haver a role of EMPLOYEE or RESEARCHER
     * 
     * @param userView
     * @return true if it has alumni and the formations list is not empty, false
     *         otherwise and if it falls under the specific cases described
     *         above
     */
    private boolean isAlumniWithNoData(User userView) {
        Person person = userView.getPerson();
        if (person.getStudent() != null && person.getStudent().getAlumni() != null && person.hasRole(RoleType.ALUMNI) != null) {
            if ((person.getTeacher() != null && person.getTeacher().isActiveContractedTeacher())
                    || person.hasRole(RoleType.EMPLOYEE) != null || person.hasRole(RoleType.RESEARCHER) != null) {
                return false;
            }
            return person.getFormations().isEmpty();
        }
        return false;
    }

    private ActionForward handleSessionCreationAndForwardToGratuityPaymentsReminder(HttpServletRequest request, User userView,
            HttpSession session) {
        return new ActionForward("/gratuityPaymentsReminder.do?method=showReminder");
    }

    private boolean isStudentAndHasGratuityDebtsToPay(final User userView) {
        return userView.getPerson().hasRole(RoleType.STUDENT)
                && userView.getPerson().hasGratuityOrAdministrativeOfficeFeeAndInsuranceDebtsFor(
                        ExecutionYear.readCurrentExecutionYear());
    }

    private boolean isTeacherAndHasInquiriesToRespond(User userView) {
        if (userView.getPerson().hasRole(RoleType.TEACHER)
                || (TeacherInquiryTemplate.getCurrentTemplate() != null && !userView.getPerson()
                        .getProfessorships(TeacherInquiryTemplate.getCurrentTemplate().getExecutionPeriod()).isEmpty())) {
            return !TeacherInquiryTemplate.getExecutionCoursesWithTeachingInquiriesToAnswer(userView.getPerson()).isEmpty();
        }
        return false;
    }

    private boolean isRegentAndHasInquiriesToRespond(User userView) {
        if (userView.getPerson().hasRole(RoleType.TEACHER)
                || (RegentInquiryTemplate.getCurrentTemplate() != null && !userView.getPerson()
                        .getProfessorships(RegentInquiryTemplate.getCurrentTemplate().getExecutionPeriod()).isEmpty())) {
            return !RegentInquiryTemplate.getExecutionCoursesWithRegentInquiriesToAnswer(userView.getPerson()).isEmpty();
        }
        return false;
    }

    private boolean isStudentAndHasQucInquiriesToRespond(final User userView) {
        if (userView.getPerson().hasRole(RoleType.STUDENT)) {
            final Student student = userView.getPerson().getStudent();
            return student != null && StudentInquiryRegistry.hasInquiriesToRespond(student);
        }
        return false;
    }

    private boolean isDelegateAndHasInquiriesToRespond(final User userView) {
        if (userView.getPerson().hasRole(RoleType.DELEGATE)) {
            final Student student = userView.getPerson().getStudent();
            return student != null && DelegateInquiryTemplate.hasYearDelegateInquiriesToAnswer(student);
        }
        return false;
    }

    protected ActionForward getAuthenticationFailedForward(final ActionMapping mapping, final HttpServletRequest request,
            final String actionKey, final String messageKey) {
        Authenticate.logout(request.getSession());
        return new ActionForward("/authenticationFailed.jsp");
    }

    private ActionForward handleSessionCreationAndGetForward(ActionMapping mapping, HttpServletRequest request, User userView,
            final HttpSession session) {
        return new ActionForward("/home.do", true);
    }

    private ActionForward handleSessionCreationAndForwardToTeachingService(HttpServletRequest request, User userView,
            HttpSession session) {
        String teacherOid = userView.getPerson().getTeacher().getExternalId();
        String executionYearOid = ExecutionYear.readCurrentExecutionYear().getExternalId();

        HtmlLink link = new HtmlLink();
        link.setModule("/departmentMember");
        link.setUrl("/credits.do?method=viewAnnualTeachingCredits&teacherOid=" + teacherOid + "&executionYearOid="
                + executionYearOid);
        link.setEscapeAmpersand(false);
        String calculatedUrl = link.calculateUrl();
        return new ActionForward("/departmentMember/credits.do?method=viewAnnualTeachingCredits&teacherOid=" + teacherOid
                + "&executionYearOid=" + executionYearOid + "&_request_checksum_="
                + GenericChecksumRewriter.calculateChecksum(calculatedUrl, session), true);
    }

    private ActionForward handleSessionCreationAndForwardToPendingTeachingReductionService(HttpServletRequest request,
            User userView, HttpSession session) {
        HtmlLink link = new HtmlLink();
        link.setModule("/departmentMember");
        link.setUrl("/creditsReductions.do?method=showReductionServices");
        link.setEscapeAmpersand(false);
        String calculatedUrl = link.calculateUrl();
        return new ActionForward("/departmentMember/creditsReductions.do?method=showReductionServices&_request_checksum_="
                + GenericChecksumRewriter.calculateChecksum(calculatedUrl, session), true);
    }

    private ActionForward handleSessionCreationAndForwardToRAIDESInquiriesResponseQuestion(HttpServletRequest request,
            User userView, HttpSession session) {
        HtmlLink link = new HtmlLink();
        link.setModule("/student");
        link.setUrl("/editMissingCandidacyInformation.do?method=prepareEdit");
        link.setEscapeAmpersand(false);
        String calculatedUrl = link.calculateUrl();
        return new ActionForward("/student/editMissingCandidacyInformation.do?method=prepareEdit&_request_checksum_="
                + GenericChecksumRewriter.calculateChecksum(calculatedUrl, session), true);
    }

    private ActionForward handleSessionCreationAndForwardToAlumniInquiriesResponseQuestion(HttpServletRequest request,
            User userView, HttpSession session) {
        return new ActionForward("/respondToAlumniInquiriesQuestion.do?method=showQuestion");
    }

    private ActionForward handleSessionCreationAndForwardToQucInquiriesResponseQuestion(HttpServletRequest request,
            User userView, HttpSession session) {
        return new ActionForward("/respondToInquiriesQuestion.do?method=showQuestion");
    }

    private ActionForward handleSessionCreationAndForwardToDelegateInquiriesResponseQuestion(HttpServletRequest request,
            User userView, HttpSession session) {
        return new ActionForward("/respondToYearDelegateInquiriesQuestion.do?method=showQuestion");
    }

    private ActionForward handleSessionCreationAndForwardToTeachingInquiriesResponseQuestion(HttpServletRequest request,
            User userView, HttpSession session) {
        return new ActionForward("/respondToTeachingInquiriesQuestion.do?method=showQuestion");
    }

    private ActionForward handleSessionCreationAndForwardToRegentInquiriesResponseQuestion(HttpServletRequest request,
            User userView, HttpSession session) {
        return new ActionForward("/respondToRegentInquiriesQuestion.do?method=showQuestion");
    }
}