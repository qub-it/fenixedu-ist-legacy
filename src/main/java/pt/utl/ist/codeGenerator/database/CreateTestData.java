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
package pt.utl.ist.codeGenerator.database;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.fenixedu.academic.dto.GenericPair;
import org.fenixedu.academic.dto.teacher.professorship.SupportLessonDTO;
import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.BibliographicReference;
import org.fenixedu.academic.domain.Branch;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CompetenceCourseType;
import org.fenixedu.academic.domain.Coordinator;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.CourseLoad;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.CurricularCourseScope;
import org.fenixedu.academic.domain.CurricularSemester;
import org.fenixedu.academic.domain.CurricularYear;
import org.fenixedu.academic.domain.Curriculum;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.domain.DegreeModuleScope;
import org.fenixedu.academic.domain.Department;
import org.fenixedu.academic.domain.Employee;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentPeriodInClasses;
import org.fenixedu.academic.domain.EnrolmentPeriodInCurricularCourses;
import org.fenixedu.academic.domain.EnrolmentPeriodInCurricularCoursesSpecialSeason;
import org.fenixedu.academic.domain.EvaluationMethod;
import org.fenixedu.academic.domain.Exam;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.FrequencyType;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.Lesson;
import org.fenixedu.academic.domain.LessonPlanning;
import org.fenixedu.academic.domain.OccupationPeriod;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.ShiftProfessorship;
import org.fenixedu.academic.domain.ShiftType;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.SupportLesson;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.WrittenEvaluation;
import org.fenixedu.academic.domain.WrittenTest;
import org.fenixedu.academic.domain.accounting.EntryType;
import org.fenixedu.academic.domain.accounting.EventType;
import org.fenixedu.academic.domain.accounting.events.AdministrativeOfficeFeeAndInsuranceEvent;
import org.fenixedu.academic.domain.accounting.events.gratuity.GratuityEventWithPaymentPlan;
import org.fenixedu.academic.domain.accounting.installments.InstallmentWithMonthlyPenalty;
import org.fenixedu.academic.domain.accounting.paymentPlans.FullGratuityPaymentPlan;
import org.fenixedu.academic.domain.accounting.paymentPlans.GratuityPaymentPlan;
import org.fenixedu.academic.domain.accounting.paymentPlans.GratuityPaymentPlanForStudentsEnroledOnlyInSecondSemester;
import org.fenixedu.academic.domain.accounting.postingRules.gratuity.GratuityWithPaymentPlanPR;
import org.fenixedu.academic.domain.accounting.serviceAgreementTemplates.AdministrativeOfficeServiceAgreementTemplate;
import org.fenixedu.academic.domain.accounting.serviceAgreementTemplates.DegreeCurricularPlanServiceAgreementTemplate;
import org.fenixedu.academic.domain.accounting.serviceAgreements.DegreeCurricularPlanServiceAgreement;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.branch.BranchType;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curriculum.CurricularCourseType;
import org.fenixedu.academic.domain.curriculum.EnrollmentCondition;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degree.degreeCurricularPlan.DegreeCurricularPlanState;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseInformation;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseLevel;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseLoad;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CurricularStage;
import org.fenixedu.academic.domain.degreeStructure.CycleCourseGroup;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.degreeStructure.RegimeType;
import org.fenixedu.academic.domain.degreeStructure.RootCourseGroup;
import org.fenixedu.academic.domain.organizationalStructure.Accountability;
import org.fenixedu.academic.domain.organizationalStructure.AccountabilityType;
import org.fenixedu.academic.domain.organizationalStructure.AccountabilityTypeEnum;
import org.fenixedu.academic.domain.organizationalStructure.AggregateUnit;
import org.fenixedu.academic.domain.organizationalStructure.CompetenceCourseGroupUnit;
import org.fenixedu.academic.domain.organizationalStructure.CountryUnit;
import org.fenixedu.academic.domain.organizationalStructure.DegreeUnit;
import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.academic.domain.organizationalStructure.EmployeeContract;
import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.academic.domain.organizationalStructure.SchoolUnit;
import org.fenixedu.academic.domain.organizationalStructure.ScientificAreaUnit;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UniversityUnit;
import org.fenixedu.academic.domain.person.RoleType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.teacher.TeacherService;
import org.fenixedu.academic.domain.time.calendarStructure.AcademicCalendarRootEntry;
import org.fenixedu.academic.domain.time.calendarStructure.AcademicPeriod;
import org.fenixedu.academic.domain.time.calendarStructure.AcademicSemesterCE;
import org.fenixedu.academic.domain.time.calendarStructure.AcademicYearCE;
import org.fenixedu.academic.domain.vigilancy.Vigilancy;
import org.fenixedu.academic.domain.vigilancy.VigilantGroup;
import org.fenixedu.academic.util.DiaSemana;
import org.fenixedu.academic.util.HourMinuteSecond;
import org.fenixedu.academic.util.MarkType;
import org.fenixedu.academic.util.Money;
import org.fenixedu.academic.util.PeriodState;
import org.fenixedu.academic.util.Season;

import org.fenixedu.bennu.core.bootstrap.AdminUserBootstrapper.AdminUserSection;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.portal.domain.PortalBootstrapper.PortalSection;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.spaces.domain.Space;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.YearMonthDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.utl.ist.codeGenerator.database.FenixBootstrapper.SchoolSetupSection;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class CreateTestData {

    private static final Logger logger = LoggerFactory.getLogger(CreateTestData.class);

    public static abstract class AtomicAction extends Thread {
        @Atomic
        @Override
        public void run() {
            doIt();
        }

        public abstract void doIt();
    }

    public static void doAction(AtomicAction action) {
        action.start();
        try {
            action.join();
        } catch (InterruptedException ie) {
            logger.warn("Caught an interrupt during the execution of an atomic action, but proceeding anyway...");
        }
    }

    private static Space getCampus() {
        if (Space.getAllCampus().isEmpty()) {
            throw new Error("Could not find another campus.");
        }

        return Space.getAllCampus().iterator().next();
    }

    private static void setPrivledges() {
        // UserView.setUser(new MockUserView());
    }

    private static Bennu getRootDomainObject() {
        return Bennu.getInstance();
    }

    public static class CreateExecutionYears extends AtomicAction {
        @Override
        public void doIt() {
            I18N.setLocale(Locale.getDefault());

            final int numYearsToCreate = 5;
            final YearMonthDay today = new YearMonthDay();
            final YearMonthDay yearMonthDay = new YearMonthDay(today.getYear() - numYearsToCreate + 2, 9, 1);
            AcademicCalendarRootEntry rootEntry =
                    new AcademicCalendarRootEntry(new MultiLanguageString("Calendario Academico"), null, null);
            for (int i = 0; i < numYearsToCreate; createExecutionYear(yearMonthDay, i++, rootEntry)) {
                ;
            }
        }

        private void createExecutionYear(final YearMonthDay yearMonthDay, final int offset, AcademicCalendarRootEntry rootEntry) {

            final int year = yearMonthDay.getYear() + offset;
            final YearMonthDay start = new YearMonthDay(year, yearMonthDay.getMonthOfYear(), yearMonthDay.getDayOfMonth());
            final YearMonthDay end = new YearMonthDay(year + 1, 8, 31);

            AcademicYearCE academicYear =
                    new AcademicYearCE(rootEntry, new MultiLanguageString(getYearString(year)), null,
                            start.toDateTimeAtMidnight(), end.toDateTimeAtMidnight(), rootEntry);

            ExecutionYear executionYear = ExecutionYear.getExecutionYear(academicYear);

            final YearMonthDay now = new YearMonthDay();
            if (start.isAfter(now) || end.isBefore(now)) {
                executionYear.setState(PeriodState.OPEN);
            } else {
                executionYear.setState(PeriodState.CURRENT);
            }

            createExecutionPeriods(executionYear, academicYear);
        }

        private void createExecutionPeriods(final ExecutionYear executionYear, AcademicYearCE academicYear) {
            createExecutionPeriods(executionYear, 1, academicYear);
            createExecutionPeriods(executionYear, 2, academicYear);
        }

        private void createExecutionPeriods(final ExecutionYear executionYear, final int semester, AcademicYearCE academicYear) {

            final YearMonthDay start = getStartYearMonthDay(executionYear, semester);
            final YearMonthDay end = getEndYearMonthDay(executionYear, semester);

            AcademicSemesterCE academicSemester =
                    new AcademicSemesterCE(academicYear, new MultiLanguageString(getPeriodString(semester)), null,
                            start.toDateTimeAtMidnight(), end.toDateTimeAtMidnight(), academicYear.getRootEntry());

            ExecutionSemester executionPeriod = ExecutionSemester.getExecutionPeriod(academicSemester);

            final YearMonthDay now = new YearMonthDay();
            if (start.isAfter(now) || end.isBefore(now)) {
                executionPeriod.setState(PeriodState.OPEN);
            } else {
                executionPeriod.setState(PeriodState.CURRENT);
            }
        }

        private YearMonthDay getStartYearMonthDay(final ExecutionYear executionYear, final int semester) {
            final YearMonthDay yearMonthDay = executionYear.getBeginDateYearMonthDay();
            return semester == 1 ? yearMonthDay : new YearMonthDay(yearMonthDay.getYear() + 1, 2, 1);
        }

        private YearMonthDay getEndYearMonthDay(final ExecutionYear executionYear, final int semester) {
            final YearMonthDay yearMonthDay = executionYear.getEndDateYearMonthDay();
            return semester == 2 ? yearMonthDay : new YearMonthDay(yearMonthDay.getYear(), 1, 31);
        }

        private String getPeriodString(final int semester) {
            return "Semester " + semester;
        }

        private String getYearString(final int year) {
            return Integer.toString(year) + '/' + (year + 1);
        }
    }

//    public static class CreateResources extends AtomicAction {
//        private int roomCounter = 0;
//
//        Group managersGroup = null;
//
//        @Override
//        public void doIt() {
//            managersGroup = getRoleGroup(RoleType.MANAGER);
//            createCampi(1);
//            createCampi(2);
//        }
//
//        private void createCampi(int i) {
//            final org.fenixedu.spaces.domain.Space campus = new org.fenixedu.spaces.domain.Space("Herdade do Conhecimento " + i, new YearMonthDay(), null, null);
//            for (int j = i; j < i + 3; j++) {
//                createBuilding(campus, j);
//            }
//        }
//
//        private void createBuilding(final org.fenixedu.spaces.domain.Space campus, final int j) {
//            final Building building = new Building(campus, "Building " + j, new YearMonthDay(), null, null, "");
//            for (int k = -1; k < 2; k++) {
//                createFloor(building, k);
//            }
//        }
//
//        private void createFloor(final Building building, final int k) {
//            final Floor floor = new Floor(building, k, new YearMonthDay(), null, null);
//            for (int l = 0; l < 25; l++) {
//                createRoom(floor);
//            }
//        }
//
//        private void createRoom(Floor floor) {
//            final Room room =
//                    new Room(floor, null, getRoomName(), "", /* RoomClassification */null, new BigDecimal(30), Boolean.TRUE,
//                            Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, "", new YearMonthDay(), null,
//                            Integer.toString(roomCounter - 1), null, null);
//            room.setExtensionOccupationsAccessGroup(managersGroup);
//            room.setGenericEventOccupationsAccessGroup(managersGroup);
//            room.setLessonOccupationsAccessGroup(managersGroup);
//            room.setPersonOccupationsAccessGroup(managersGroup);
//            room.setSpaceManagementAccessGroup(managersGroup);
//            room.setUnitOccupationsAccessGroup(managersGroup);
//            room.setWrittenEvaluationOccupationsAccessGroup(managersGroup);
//            lessonRoomManager.push(room);
//            examRoomManager.add(room);
//            writtenTestsRoomManager.add(room);
//        }
//
//        private String getRoomName() {
//            return "Room" + roomCounter++;
//        }
//    }

    static AdministrativeOffice administrativeOffice;

    public static class CreateOrganizationalStructure {
        public void doIt(PortalSection portalSection, SchoolSetupSection schoolSetupSection) {
            final CountryUnit countryUnit = getCountryUnit(Country.readDefault().getName());
            final UniversityUnit universityUnit =
                    createUniversityUnit(countryUnit, schoolSetupSection.getUniversityName(),
                            schoolSetupSection.getUniversityAcronym());
            final SchoolUnit institutionUnit =
                    createSchoolUnit(universityUnit, portalSection.getOrganizationName(), schoolSetupSection.getSchoolAcronym());
            getRootDomainObject().setInstitutionUnit(institutionUnit);
            final AggregateUnit serviceUnits = createAggregateUnit(institutionUnit, "Services");
            //createServiceUnits(serviceUnits);
            final AggregateUnit departmentUnits = createAggregateUnit(institutionUnit, "Departments");
            //createDepartmentUnits(departmentUnits);
            final AggregateUnit degreeUnits = createAggregateUnit(institutionUnit, "Degrees");
            //createDegreeUnits(degreeUnits);
        }

        private CountryUnit getCountryUnit(final String countryUnitName) {
            for (final Party party : getRootDomainObject().getPartysSet()) {
                if (party.isCountryUnit()) {
                    final CountryUnit countryUnit = (CountryUnit) party;
                    if (countryUnit.getName().equalsIgnoreCase(countryUnitName)) {
                        return countryUnit;
                    }
                }
            }
            return null;
        }

        private UniversityUnit createUniversityUnit(final CountryUnit countryUnit, final String universityName,
                final String universityAcronym) {
            return UniversityUnit.createNewUniversityUnit(new MultiLanguageString(Locale.getDefault(), universityName), null,
                    null, universityAcronym, new YearMonthDay(), null, countryUnit, null, null, false, null);
        }

        private AggregateUnit createAggregateUnit(final Unit parentUnit, final String unitName) {
            return AggregateUnit.createNewAggregateUnit(new MultiLanguageString(Locale.getDefault(), unitName), null, null, null,
                    new YearMonthDay(), null, parentUnit,
                    AccountabilityType.readByType(AccountabilityTypeEnum.ORGANIZATIONAL_STRUCTURE), null, null, Boolean.FALSE,
                    null);
        }

        private SchoolUnit createSchoolUnit(final UniversityUnit universityUnit, final String universityName,
                final String universityAcronym) {
            return SchoolUnit.createNewSchoolUnit(new MultiLanguageString(Locale.getDefault(), universityName), null, null,
                    universityAcronym, new YearMonthDay(), null, universityUnit, null, null, Boolean.FALSE, null);
        }

        private void createServiceUnits(final AggregateUnit serviceUnits) {
            administrativeOffice = new AdministrativeOffice();
            Unit.createNewUnit(new MultiLanguageString(Locale.getDefault(), "Office"), null, null, null, new YearMonthDay(),
                    null, serviceUnits, AccountabilityType.readByType(AccountabilityTypeEnum.ADMINISTRATIVE_STRUCTURE), null,
                    null, administrativeOffice, Boolean.FALSE, null);
            new AdministrativeOfficeServiceAgreementTemplate(administrativeOffice);
        }

        private void createDepartmentUnits(final AggregateUnit departmentUnits) {
            for (int i = 0; i < 5; i++) {
                createDepartment(departmentUnits, i);
            }
        }

        private void createDepartment(final AggregateUnit departmentUnits, final int i) {
            final Department department = new Department();
            department.setCode(getDepartmentCode(i));
            final String departmentName = getDepartmentName(i);
            department.setName(departmentName);
            department.setRealName(departmentName);
            department.setCompetenceCourseMembersGroup(getCompetenceCourseMembersGroup());

            final DepartmentUnit departmentUnit = createDepartmentUnut(departmentUnits, 3020 + i, department);
            department.setDepartmentUnit(departmentUnit);

            createCompetenceCourseGroupUnit(departmentUnit);
        }

        private int areaCounter = 0;

        private void createCompetenceCourseGroupUnit(final DepartmentUnit departmentUnit) {
            final ScientificAreaUnit scientificAreaUnit =
                    ScientificAreaUnit.createNewInternalScientificArea(new MultiLanguageString(Locale.getDefault(),
                            "Scientific Area"), null, null, "Code" + areaCounter++, new YearMonthDay(), null, departmentUnit,
                            AccountabilityType.readByType(AccountabilityTypeEnum.ACADEMIC_STRUCTURE), null, null, Boolean.FALSE,
                            null);

            CompetenceCourseGroupUnit.createNewInternalCompetenceCourseGroupUnit(new MultiLanguageString(Locale.getDefault(),
                    "Competence Courses"), null, null, null, new YearMonthDay(), null, scientificAreaUnit, AccountabilityType
                    .readByType(AccountabilityTypeEnum.ACADEMIC_STRUCTURE), null, null, Boolean.FALSE, null);
        }

        private DepartmentUnit createDepartmentUnut(final AggregateUnit departmentUnits, final int someNumber,
                final Department department) {
            return DepartmentUnit.createNewInternalDepartmentUnit(new MultiLanguageString(Locale.getDefault(), "Department Name "
                    + someNumber), null, Integer.valueOf(2100 + someNumber), "DU" + someNumber,
                    new YearMonthDay().minusMonths(1), null, departmentUnits,
                    AccountabilityType.readByType(AccountabilityTypeEnum.ACADEMIC_STRUCTURE), null, department, null,
                    Boolean.FALSE, null);
        }

        private org.fenixedu.bennu.core.groups.Group getCompetenceCourseMembersGroup() {
            return RoleType.TEACHER.actualGroup().or(RoleType.MANAGER.actualGroup());
        }

        private String getDepartmentName(final int i) {
            return "Department " + i;
        }

        private String getDepartmentCode(final int i) {
            return "DEP" + i;
        }

        private void createDegreeUnits(final AggregateUnit degreeUnits) {
            for (final DegreeType degreeType : DegreeType.NOT_EMPTY_VALUES) {
                createAggregateUnit(degreeUnits, degreeType.getName());
            }
        }
    }

    private static Group getRoleGroup(final RoleType roleType) {
        return roleType.actualGroup();
    }

    public static class CreateDegrees {
        public void doIt(AdminUserSection adminSection) {
            I18N.setLocale(Locale.getDefault());

            final Unit unit = findUnitByName("Degrees");
            for (final DegreeType degreeType : DegreeType.NOT_EMPTY_VALUES) {
                if (degreeType.isBolonhaType()) {
                    for (int i = 0; i < 1; i++) {
                        final Degree degree = createDegree(degreeType, (degreeType.ordinal() * 10) + i);
                        createDegreeInfo(degree);
                        associateToDepartment(degree);

                        final DegreeCurricularPlan degreeCurricularPlan = createDegreeCurricularPlan(degree, adminSection);

                        createExecutionDegrees(degreeCurricularPlan, getCampus());
                        degree.setAdministrativeOffice(administrativeOffice);

                        DegreeUnit.createNewInternalDegreeUnit(new MultiLanguageString(Locale.getDefault(), degree.getName()),
                                null, null, degree.getSigla(), new YearMonthDay(), null, unit,
                                AccountabilityType.readByType(AccountabilityTypeEnum.ACADEMIC_STRUCTURE), null, degree, null,
                                Boolean.FALSE, null);
                    }
                }
            }
        }

        private Unit findUnitByName(final String unitName) {
            for (final Party party : Bennu.getInstance().getPartysSet()) {
                if (party.isAggregateUnit() && party.getName().equals(unitName)) {
                    return (Unit) party;
                }
            }
            return null;
        }

        private Degree createDegree(final DegreeType degreeType, final int i) {
            return new Degree("Agricultura do Conhecimento " + i, "Knowledge Agriculture " + i, "CODE" + i, degreeType,
                    degreeType.getGradeScale());
        }

        private DegreeCurricularPlan createDegreeCurricularPlan(final Degree degree, AdminUserSection adminSection) {

            Person person = Person.findByUsername(adminSection.getAdminUsername());
            final DegreeCurricularPlan degreeCurricularPlan =
                    degree.createBolonhaDegreeCurricularPlan(degree.getSigla(), GradeScale.TYPE20, person);

            degreeCurricularPlan.setCurricularStage(CurricularStage.APPROVED);
            degreeCurricularPlan.setDescription("Bla bla bla. Desc. do plano curricular do curso. Bla bla bla");
            degreeCurricularPlan.setDescriptionEn("Blur ble bla. Description of the degrees curricular plan. Goo goo foo foo.");
            degreeCurricularPlan.setState(DegreeCurricularPlanState.ACTIVE);

            if (degree.getDegreeType().isBolonhaType()) {
                RootCourseGroup.createRoot(degreeCurricularPlan, degree.getSigla(), degree.getSigla());
            }

            return degreeCurricularPlan;
        }

        private void createExecutionDegrees(final DegreeCurricularPlan degreeCurricularPlan, final Space campus) {
            for (final ExecutionYear executionYear : getRootDomainObject().getExecutionYearsSet()) {
                final ExecutionDegree executionDegree =
                        degreeCurricularPlan.createExecutionDegree(executionYear, campus, Boolean.FALSE);
                for (final ExecutionSemester executionPeriod : executionYear.getExecutionPeriodsSet()) {
                    final Degree degree = executionDegree.getDegree();
                    for (int y = 1; y <= degree.getDegreeType().getYears(); y++) {
                        for (int i = 0; i < 1; i++) {
                            final String name = degree.getSigla() + y + executionPeriod.getSemester() + i;
                            new SchoolClass(executionDegree, executionPeriod, name, Integer.valueOf(y));
                        }
                    }
                }
                createPeriodsForExecutionDegree(executionDegree);
                createEnrolmentPeriods(degreeCurricularPlan, executionYear);
            }
        }

        private static void createPeriodsForExecutionDegree(ExecutionDegree executionDegree) {
            final ExecutionYear executionYear = executionDegree.getExecutionYear();
            final ExecutionSemester executionPeriod1 = executionYear.getFirstExecutionPeriod();
            final ExecutionSemester executionPeriod2 = executionYear.getLastExecutionPeriod();

            final OccupationPeriod occupationPeriod1 =
                    readOccupationPeriod(executionPeriod1.getBeginDateYearMonthDay(), executionPeriod1.getEndDateYearMonthDay()
                            .minusDays(32));
            final OccupationPeriod occupationPeriod2 =
                    readOccupationPeriod(executionPeriod1.getEndDateYearMonthDay().minusDays(31),
                            executionPeriod1.getEndDateYearMonthDay());
            final OccupationPeriod occupationPeriod3 =
                    readOccupationPeriod(executionPeriod2.getBeginDateYearMonthDay(), executionPeriod2.getEndDateYearMonthDay()
                            .minusDays(32));
            final OccupationPeriod occupationPeriod4 =
                    readOccupationPeriod(executionPeriod2.getEndDateYearMonthDay().minusDays(31),
                            executionPeriod2.getEndDateYearMonthDay());
            final OccupationPeriod occupationPeriod5 =
                    readOccupationPeriod(executionPeriod2.getEndDateYearMonthDay().plusDays(31), executionPeriod2
                            .getEndDateYearMonthDay().plusDays(46));

            executionDegree.setPeriodLessonsFirstSemester(occupationPeriod1);
            executionDegree.setPeriodExamsFirstSemester(occupationPeriod2);
            executionDegree.setPeriodLessonsSecondSemester(occupationPeriod3);
            executionDegree.setPeriodExamsSecondSemester(occupationPeriod4);
            executionDegree.setPeriodExamsSpecialSeason(occupationPeriod5);
        }

        private static OccupationPeriod readOccupationPeriod(YearMonthDay yearMonthDay1, YearMonthDay yearMonthDay2) {
            OccupationPeriod occupationPeriod = OccupationPeriod.readOccupationPeriod(yearMonthDay1, yearMonthDay2);
            return occupationPeriod == null ? new OccupationPeriod(yearMonthDay1, yearMonthDay2) : occupationPeriod;
        }

        private static void createEnrolmentPeriods(final DegreeCurricularPlan degreeCurricularPlan,
                final ExecutionYear executionYear) {
            for (final ExecutionSemester executionPeriod : executionYear.getExecutionPeriodsSet()) {
                final Date start = executionPeriod.getBeginDateYearMonthDay().toDateMidnight().toDate();
                final Date end = executionPeriod.getEndDateYearMonthDay().toDateMidnight().toDate();

                new EnrolmentPeriodInClasses(degreeCurricularPlan, executionPeriod, start, end);
                new EnrolmentPeriodInCurricularCourses(degreeCurricularPlan, executionPeriod, start, end);
                new EnrolmentPeriodInCurricularCoursesSpecialSeason(degreeCurricularPlan, executionPeriod, start, end);
            }
        }

        Space campus = null;

        Iterator<Department> departmentIterator = null;

        private void associateToDepartment(final Degree degree) {
            if (departmentIterator == null || !departmentIterator.hasNext()) {
                departmentIterator = getRootDomainObject().getDepartmentsSet().iterator();
            }
            final Department department = departmentIterator.next();
            department.addDegrees(degree);
        }

        private void createDegreeInfo(final Degree degree) {
            for (final ExecutionYear executionYear : Bennu.getInstance().getExecutionYearsSet()) {
                final DegreeInfo degreeInfo = degree.createCurrentDegreeInfo();
                degreeInfo.setExecutionYear(executionYear);
                degreeInfo.setDescription(new MultiLanguageString(MultiLanguageString.pt, "Desc. do curso. Bla bla bla bla bla.")
                        .with(MultiLanguageString.en, "Description of the degree. Blur blur blur and more blur."));
                degreeInfo.setHistory(new MultiLanguageString(MultiLanguageString.pt, "Historial do curso. Bla bla bla bla bla.")
                        .with(MultiLanguageString.en, "History of the degree. Blur blur blur and more blur."));
                degreeInfo.setObjectives(new MultiLanguageString(MultiLanguageString.pt,
                        "Objectivos do curso. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Objectives of the degree. Blur blur blur and more blur."));
                degreeInfo.setDesignedFor(new MultiLanguageString(MultiLanguageString.pt, "Prop. do curso. Bla bla bla bla bla.")
                        .with(MultiLanguageString.en, "Purpose of the degree. Blur blur blur and more blur."));
                degreeInfo.setProfessionalExits(new MultiLanguageString(MultiLanguageString.pt,
                        "Saidas profissionais. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Professional exists of the degree. Blur blur blur and more blur."));
                degreeInfo.setOperationalRegime(new MultiLanguageString(MultiLanguageString.pt,
                        "Regime operacional. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Operational regime of the degree. Blur blur blur and more blur."));
                degreeInfo.setGratuity(new MultiLanguageString(MultiLanguageString.pt, "Propinas. Bla bla bla bla bla.").with(
                        MultiLanguageString.en, "Gratuity of the degree. Blur blur blur and more blur."));
                degreeInfo.setSchoolCalendar(new MultiLanguageString(MultiLanguageString.pt,
                        "Calendario escolar. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "School calendar of the degree. Blur blur blur and more blur."));
                degreeInfo.setCandidacyPeriod(new MultiLanguageString(MultiLanguageString.pt,
                        "Periodo de candidaturas. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Candidacy period of the degree. Blur blur blur and more blur."));
                degreeInfo.setSelectionResultDeadline(new MultiLanguageString(MultiLanguageString.pt,
                        "Prazo de publicacao de resultados de candidaturas. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Seletion result deadline of the degree. Blur blur blur and more blur."));
                degreeInfo.setEnrolmentPeriod(new MultiLanguageString(MultiLanguageString.pt,
                        "Periodo de inscricoes. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Enrolment period of the degree. Blur blur blur and more blur."));
                degreeInfo.setAdditionalInfo(new MultiLanguageString(MultiLanguageString.pt,
                        "Informacao adicional. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Additional information of the degree. Blur blur blur and more blur."));
                degreeInfo.setLinks(new MultiLanguageString(MultiLanguageString.pt, "Links. Bla bla bla bla bla.").with(
                        MultiLanguageString.en, "Links of the degree. Blur blur blur and more blur."));
                degreeInfo.setTestIngression(new MultiLanguageString(MultiLanguageString.pt,
                        "Testes de ingressao. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Ingression tests of the degree. Blur blur blur and more blur."));
                degreeInfo.setClassifications(new MultiLanguageString(MultiLanguageString.pt,
                        "Classificacoes. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Classifications of the degree. Blur blur blur and more blur."));
                degreeInfo.setAccessRequisites(new MultiLanguageString(MultiLanguageString.pt,
                        "Requisitos de acesso. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Access requisites of the degree. Blur blur blur and more blur."));
                degreeInfo.setCandidacyDocuments(new MultiLanguageString(MultiLanguageString.pt,
                        "Documentos de candidatura. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Candidacy documents of the degree. Blur blur blur and more blur."));
                degreeInfo.setDriftsInitial(Integer.valueOf(1));
                degreeInfo.setDriftsFirst(Integer.valueOf(1));
                degreeInfo.setDriftsSecond(Integer.valueOf(1));
                degreeInfo.setMarkMin(Double.valueOf(12));
                degreeInfo.setMarkMax(Double.valueOf(20));
                degreeInfo.setMarkAverage(Double.valueOf(15));
                degreeInfo.setQualificationLevel(new MultiLanguageString(MultiLanguageString.pt,
                        "Nivel de qualificacao. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Qualification level of the degree. Blur blur blur and more blur."));
                degreeInfo.setRecognitions(new MultiLanguageString(MultiLanguageString.pt,
                        "Reconhecimentos. Bla bla bla bla bla.").with(MultiLanguageString.en,
                        "Recognitions of the degree. Blur blur blur and more blur."));
            }
        }
    }

    public static class CreateCurricularPeriods extends AtomicAction {

        @Override
        public void doIt() {
            for (final AcademicPeriod academicPeriod : AcademicPeriod.values()) {
                final float weight = academicPeriod.getWeight();
                if (weight > 1) {
                    final CurricularPeriod curricularPeriod = new CurricularPeriod(academicPeriod, null, null);
                    for (int i = 1; i <= academicPeriod.getWeight(); i++) {
                        final CurricularPeriod curricularYear =
                                new CurricularPeriod(AcademicPeriod.YEAR, Integer.valueOf(i), curricularPeriod);
                        for (int j = 1; j <= 2; j++) {
                            new CurricularPeriod(AcademicPeriod.SEMESTER, Integer.valueOf(j), curricularYear);
                        }
                    }
                } else if (weight == 1) {
                    final CurricularPeriod curricularPeriod = new CurricularPeriod(academicPeriod, null, null);
                    for (int j = 1; j <= 2; j++) {
                        new CurricularPeriod(AcademicPeriod.SEMESTER, Integer.valueOf(j), curricularPeriod);
                    }
                }
            }
        }

    }

    public static class CreateCurricularStructure extends AtomicAction {

        @Override
        public void doIt() {
            for (final DegreeCurricularPlan degreeCurricularPlan : DegreeCurricularPlan.readNotEmptyDegreeCurricularPlans()) {
                createCurricularCourses(degreeCurricularPlan);
            }
        }

        private void createCurricularCourses(final DegreeCurricularPlan degreeCurricularPlan) {
            final DegreeType degreeType = degreeCurricularPlan.getDegreeType();
            for (int y = 1; y <= degreeType.getYears(); y++) {
                for (int s = 1; s <= 2; s++) {
                    for (int i = 0; i < 3; i++) {
                        final String name = getCurricularCourseName();
                        final String code = getCurricularCourseCode(degreeCurricularPlan, y, s);
                        final CurricularCourse curricularCourse =
                                degreeCurricularPlan.createCurricularCourse(name, code, code, Boolean.TRUE,
                                        CurricularStage.PUBLISHED);
                        curricularCourse.setType(CurricularCourseType.NORMAL_COURSE);
                        curricularCourse.setDegreeCurricularPlan(degreeCurricularPlan);
                        createDegreeModuleScopes(degreeCurricularPlan, curricularCourse, y, s);
                        createCompetenceCourse(degreeCurricularPlan, curricularCourse);
                    }
                }
            }
        }

        private void createDegreeModuleScopes(final DegreeCurricularPlan degreeCurricularPlan,
                final CurricularCourse curricularCourse, final int y, final int s) {
            if (degreeCurricularPlan.getRoot() != null) {
                createContext(degreeCurricularPlan, curricularCourse, y, s);
            } else {
                createCurricularCourseScope(degreeCurricularPlan, curricularCourse, y, s);
            }
        }

        private void createContext(final DegreeCurricularPlan degreeCurricularPlan, final CurricularCourse curricularCourse,
                final int y, final int s) {
            final RootCourseGroup rootCourseGroup = degreeCurricularPlan.getRoot();
            for (final CycleType cycleType : degreeCurricularPlan.getDegreeType().getCycleTypes()) {
                final CycleCourseGroup cycleCourseGroup = rootCourseGroup.getCycleCourseGroup(cycleType);
                new Context(cycleCourseGroup, curricularCourse, findCurricularPeriod(degreeCurricularPlan, y, s),
                        findFirstExecutionPeriod(), null);
            }
        }

        private CurricularPeriod findCurricularPeriod(final DegreeCurricularPlan degreeCurricularPlan, final int y, final int s) {
            final DegreeType degreeType = degreeCurricularPlan.getDegreeType();
            final AcademicPeriod academicPeriod = degreeType.getAcademicPeriod();
            for (final CurricularPeriod curricularPeriod : Bennu.getInstance().getCurricularPeriodsSet()) {
                if (curricularPeriod.getAcademicPeriod().equals(academicPeriod)) {
                    for (final CurricularPeriod curricularYear : curricularPeriod.getChildsSet()) {
                        if (curricularYear.getChildOrder().intValue() == y) {
                            for (final CurricularPeriod curricularSemester : curricularYear.getChildsSet()) {
                                if (curricularSemester.getChildOrder().intValue() == s) {
                                    return curricularSemester;
                                }
                            }
                        }
                    }
                }
            }
            for (final CurricularPeriod curricularPeriod : Bennu.getInstance().getCurricularPeriodsSet()) {
                if (curricularPeriod.getAcademicPeriod().equals(academicPeriod) && academicPeriod.equals(AcademicPeriod.YEAR)) {
                    if (curricularPeriod.getChildOrder() == null || curricularPeriod.getChildOrder().intValue() == y) {
                        for (final CurricularPeriod curricularSemester : curricularPeriod.getChildsSet()) {
                            if (curricularSemester.getChildOrder().intValue() == s) {
                                return curricularSemester;
                            }
                        }
                    }
                }
            }
            logger.info("found no curricular period for: " + academicPeriod + " y " + y + " s " + s);
            if (true) {
                throw new Error();
            }
            return null;
        }

        private void createCurricularCourseScope(final DegreeCurricularPlan degreeCurricularPlan,
                final CurricularCourse curricularCourse, final int y, final int s) {
            final CurricularSemester curricularSemester = findCurricularSemester(y, s);
            new CurricularCourseScope(null, curricularCourse, curricularSemester, new DateTime().minusYears(5).toCalendar(null),
                    null, "Some annotation...");
        }

        private CurricularSemester findCurricularSemester(final int y, final int s) {
            return CurricularSemester.readBySemesterAndYear(Integer.valueOf(s), Integer.valueOf(y));
        }

        private int counter = 0;

        private String getCurricularCourseName() {
            return "KnowledgeGermination" + counter;
        }

        private String getCurricularCourseCode(final DegreeCurricularPlan degreeCurricularPlan, final int y, final int s) {
            final Degree degree = degreeCurricularPlan.getDegree();
            return degree.getSigla() + '-' + y + '-' + s + '-' + counter++;
        }

        private void createCompetenceCourse(final DegreeCurricularPlan degreeCurricularPlan,
                final CurricularCourse curricularCourse) {
            final CompetenceCourseLevel competenceCourseLevel = getCompetenceCourseLevel(degreeCurricularPlan.getDegreeType());
            final CompetenceCourseGroupUnit competenceCourseGroupUnit = findCompetenceCourseGroupUnit(degreeCurricularPlan);
            final CompetenceCourse competenceCourse =
                    new CompetenceCourse(curricularCourse.getName(), curricularCourse.getName(), Boolean.TRUE,
                            RegimeType.SEMESTRIAL, competenceCourseLevel, CompetenceCourseType.REGULAR, CurricularStage.APPROVED,
                            competenceCourseGroupUnit);
            final ExecutionSemester executionPeriod = firstExecutionPeriod();
            competenceCourse.setCreationDateYearMonthDay(executionPeriod.getBeginDateYearMonthDay());
            final CompetenceCourseInformation competenceCourseInformation =
                    competenceCourse.findCompetenceCourseInformationForExecutionPeriod(null);
            competenceCourseInformation.setExecutionPeriod(executionPeriod);
            final CompetenceCourseLoad competenceCourseLoad =
                    new CompetenceCourseLoad(Double.valueOf(2), Double.valueOf(0), Double.valueOf(0), Double.valueOf(0),
                            Double.valueOf(0), Double.valueOf(0), Double.valueOf(0), Double.valueOf(0),
                            Double.valueOf(degreeCurricularPlan.getEctsCredits()), Integer.valueOf(0), AcademicPeriod.SEMESTER);
            competenceCourseInformation.addCompetenceCourseLoads(competenceCourseLoad);
            curricularCourse.setCompetenceCourse(competenceCourse);
        }

        private ExecutionSemester firstExecutionPeriod() {
            return Collections.min(Bennu.getInstance().getExecutionPeriodsSet());
        }

        private CompetenceCourseGroupUnit findCompetenceCourseGroupUnit(final DegreeCurricularPlan degreeCurricularPlan) {
            final Degree degree = degreeCurricularPlan.getDegree();
            for (final Department department : degree.getDepartmentsSet()) {
                final DepartmentUnit departmentUnit = department.getDepartmentUnit();
                for (final Accountability accountability : departmentUnit.getChildsSet()) {
                    final Party party = accountability.getChildParty();
                    if (party.isScientificAreaUnit()) {
                        for (final Accountability accountability2 : ((ScientificAreaUnit) party).getChildsSet()) {
                            final Party party2 = accountability2.getChildParty();
                            if (party2.isCompetenceCourseGroupUnit()) {
                                return (CompetenceCourseGroupUnit) party2;
                            }
                        }
                    }
                }
            }
            return null;
        }

        private CompetenceCourseLevel getCompetenceCourseLevel(final DegreeType degreeType) {
            for (final CycleType cycleType : degreeType.getCycleTypes()) {
                return cycleType == CycleType.FIRST_CYCLE ? CompetenceCourseLevel.FIRST_CYCLE : CompetenceCourseLevel.SECOND_CYCLE;
            }
            return CompetenceCourseLevel.FORMATION;
        }
    }

    public static class CreateExecutionCourses extends AtomicAction {
        @Override
        public void doIt() {
            I18N.setLocale(Locale.getDefault());

            for (final DegreeModule degreeModule : Bennu.getInstance().getDegreeModulesSet()) {
                if (degreeModule.isCurricularCourse()) {
                    final CurricularCourse curricularCourse = (CurricularCourse) degreeModule;
                    for (final ExecutionSemester executionPeriod : Bennu.getInstance().getExecutionPeriodsSet()) {
                        if (curricularCourse.hasActiveScopesInExecutionPeriod(executionPeriod)) {
                            final ExecutionCourse executionCourse =
                                    new ExecutionCourse(curricularCourse.getName(), curricularCourse.getCode(), executionPeriod,
                                            null);

                            BigDecimal numberOfWeeks = BigDecimal.valueOf(CompetenceCourseLoad.NUMBER_OF_WEEKS * 2 - 2);
                            new CourseLoad(executionCourse, ShiftType.TEORICA, BigDecimal.valueOf(1.5), BigDecimal.valueOf(1.5)
                                    .multiply(numberOfWeeks));
                            new CourseLoad(executionCourse, ShiftType.PRATICA, BigDecimal.valueOf(2), BigDecimal.valueOf(2)
                                    .multiply(numberOfWeeks));

                            curricularCourse.addAssociatedExecutionCourses(executionCourse);

                            createAnnouncementsAndPlanning(executionCourse);
                            createEvaluationMethod(executionCourse);
                            createBibliographicReferences(executionCourse);

                            createShifts(executionCourse);
                        }
                    }
                }
            }
        }

        private static void createAnnouncementsAndPlanning(final ExecutionCourse executionCourse) {
            final ExecutionSemester executionPeriod = executionCourse.getExecutionPeriod();
            final YearMonthDay start = executionPeriod.getBeginDateYearMonthDay();
            final YearMonthDay end = executionPeriod.getEndDateYearMonthDay();
            for (YearMonthDay day = start; day.compareTo(end) < 0; day = day.plusDays(Lesson.NUMBER_OF_DAYS_IN_WEEK)) {
                createPlanning(executionCourse, ShiftType.TEORICA);
                createPlanning(executionCourse, ShiftType.TEORICA);
                createPlanning(executionCourse, ShiftType.PRATICA);
            }
        }

        private static void createPlanning(ExecutionCourse executionCourse, ShiftType shiftType) {
            final LessonPlanning lessonPlanning =
                    new LessonPlanning(new MultiLanguageString(MultiLanguageString.pt, "Titulo do Planeamento").with(
                            MultiLanguageString.en, "Title of the planning."), new MultiLanguageString(MultiLanguageString.pt,
                            "Corpo do Planeamento").with(MultiLanguageString.en, "Planning contents."), shiftType,
                            executionCourse);
        }

        private static void createEvaluationMethod(final ExecutionCourse executionCourse) {
            final EvaluationMethod evaluationMethod = new EvaluationMethod();
            evaluationMethod.setExecutionCourse(executionCourse);
            evaluationMethod.setEvaluationElements(new MultiLanguageString(MultiLanguageString.pt,
                    "Metodo de avaliacao. Bla bla bla bla bla.").with(MultiLanguageString.en,
                    "Evaluation method. Blur blur ble blur bla."));
        }

        private static void createBibliographicReferences(final ExecutionCourse executionCourse) {
            createBibliographicReference(executionCourse, Boolean.FALSE);
            createBibliographicReference(executionCourse, Boolean.TRUE);
        }

        private static void createBibliographicReference(final ExecutionCourse executionCourse, final Boolean optional) {
            final BibliographicReference bibliographicReference = new BibliographicReference();
            bibliographicReference.setAuthors("Nome do Autor");
            bibliographicReference.setExecutionCourse(executionCourse);
            bibliographicReference.setOptional(optional);
            bibliographicReference.setReference("Referencia");
            bibliographicReference.setTitle("Titulo");
            bibliographicReference.setYear("Ano");
        }

        private static void createShifts(final ExecutionCourse executionCourse) {
            List<ShiftType> shiftTypes = new ArrayList<ShiftType>();
            shiftTypes.add(ShiftType.TEORICA);

            final Shift shift1 = new Shift(executionCourse, shiftTypes, Integer.valueOf(50));
            createLesson(shift1, 90);

            shiftTypes.clear();
            shiftTypes.add(ShiftType.PRATICA);

            final Shift shift2 = new Shift(executionCourse, shiftTypes, Integer.valueOf(50));
            createLesson(shift2, 120);

            for (final CurricularCourse curricularCourse : executionCourse.getAssociatedCurricularCoursesSet()) {
                final DegreeCurricularPlan degreeCurricularPlan = curricularCourse.getDegreeCurricularPlan();

                for (final DegreeModuleScope degreeModuleScope : curricularCourse.getDegreeModuleScopes()) {
                    final int year = degreeModuleScope.getCurricularYear().intValue();
                    for (final ExecutionDegree executionDegree : degreeCurricularPlan.getExecutionDegreesSet()) {
                        if (executionDegree.getExecutionYear() == executionCourse.getExecutionPeriod().getExecutionYear()) {
                            for (final SchoolClass schoolClass : executionDegree.getSchoolClassesSet()) {
                                if (schoolClass.getExecutionPeriod() == executionCourse.getExecutionPeriod()) {
                                    if (schoolClass.getAnoCurricular().intValue() == year) {
                                        shift1.addAssociatedClasses(schoolClass);
                                        shift2.addAssociatedClasses(schoolClass);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        private static void createLesson(final Shift shift, int durationInMinutes) {
            final HourMinuteSecond start = lessonRoomManager.getNextHourMinuteSecond(durationInMinutes);
            final HourMinuteSecond end = start.plusMinutes(durationInMinutes);
            final Calendar cStart = toCalendar(start);
            final Calendar cEnd = toCalendar(end);
            final DiaSemana diaSemana = new DiaSemana(lessonRoomManager.getNextWeekDay());
            final Space room = lessonRoomManager.getNextOldRoom();
            final ExecutionSemester executionPeriod = shift.getExecutionCourse().getExecutionPeriod();
            GenericPair<YearMonthDay, YearMonthDay> maxLessonsPeriod = shift.getExecutionCourse().getMaxLessonsPeriod();
            new Lesson(diaSemana, cStart, cEnd, shift, FrequencyType.WEEKLY, executionPeriod, maxLessonsPeriod.getLeft(),
                    maxLessonsPeriod.getRight(), room);
        }
    }

    public static class CreateEvaluations extends AtomicAction {
        @Override
        public void doIt() {
            //final Person person = Role.getRoleByRoleType(RoleType.RESOURCE_ALLOCATION_MANAGER).getAssociatedPersonsSet().iterator().next();
            final Person person = Bennu.getInstance().getUserSet().iterator().next().getPerson();
            //Role.getRoleByRoleType(RoleType.RESOURCE_ALLOCATION_MANAGER).addAssociatedPersons(person);
            // final User userView = new Authenticate().mock(person, "://localhost");
            // UserView.setUser(userView);

            final Bennu rootDomainObject = Bennu.getInstance();
            for (final ExecutionSemester executionPeriod : rootDomainObject.getExecutionPeriodsSet()) {
                createWrittenEvaluations(executionPeriod, new Season(Season.SEASON1), "Teste1");
                for (int i = 0; i++ < 500; writtenTestsRoomManager.getNextDateTime(executionPeriod)) {
                    ;
                }
                createWrittenEvaluations(executionPeriod, new Season(Season.SEASON2), "Teste2");
            }
        }

        private static void createWrittenEvaluations(final ExecutionSemester executionPeriod, final Season season,
                final String writtenTestName) {
            for (final ExecutionCourse executionCourse : executionPeriod.getAssociatedExecutionCoursesSet()) {
                createWrittenEvaluation(executionPeriod, executionCourse, writtenTestName);
                createExam(executionPeriod, executionCourse, season);
            }
        }

        private static void createWrittenEvaluation(final ExecutionSemester executionPeriod,
                final ExecutionCourse executionCourse, final String name) {
            DateTime startDateTime = writtenTestsRoomManager.getNextDateTime(executionPeriod);
            DateTime endDateTime = startDateTime.plusMinutes(120);
            if (startDateTime.getDayOfMonth() != endDateTime.getDayOfMonth()) {
                startDateTime = writtenTestsRoomManager.getNextDateTime(executionPeriod);
                endDateTime = startDateTime.plusMinutes(120);
            }
            final Space room = writtenTestsRoomManager.getNextOldRoom(executionPeriod);
            final List<ExecutionCourse> executionCourses = new ArrayList<ExecutionCourse>();
            executionCourses.add(executionCourse);
            final List<DegreeModuleScope> degreeModuleScopes = new ArrayList<DegreeModuleScope>();
            for (final CurricularCourse curricularCourse : executionCourse.getAssociatedCurricularCoursesSet()) {
                degreeModuleScopes.addAll(curricularCourse.getDegreeModuleScopes());
            }
            final List<Space> rooms = new ArrayList<Space>();
            rooms.add(room);
            final WrittenTest writtenTest =
                    new WrittenTest(startDateTime.toDate(), startDateTime.toDate(), endDateTime.toDate(), executionCourses,
                            degreeModuleScopes, rooms, null, name);
            createWrittenEvaluationEnrolmentPeriodAndVigilancies(executionPeriod, writtenTest, executionCourse);
        }

        private static void createExam(final ExecutionSemester executionPeriod, final ExecutionCourse executionCourse,
                final Season season) {
            DateTime startDateTime = examRoomManager.getNextDateTime(executionPeriod);
            DateTime endDateTime = startDateTime.plusMinutes(180);
            if (startDateTime.getDayOfMonth() != endDateTime.getDayOfMonth()) {
                startDateTime = examRoomManager.getNextDateTime(executionPeriod);
                endDateTime = startDateTime.plusMinutes(180);
            }
            final Space room = examRoomManager.getNextOldRoom(executionPeriod);
            final List<ExecutionCourse> executionCourses = new ArrayList<ExecutionCourse>();
            executionCourses.add(executionCourse);
            final List<DegreeModuleScope> degreeModuleScopes = new ArrayList<DegreeModuleScope>();
            for (final CurricularCourse curricularCourse : executionCourse.getAssociatedCurricularCoursesSet()) {
                degreeModuleScopes.addAll(curricularCourse.getDegreeModuleScopes());
            }
            final List<Space> rooms = new ArrayList<Space>();
            // rooms.add(room);
            final Exam exam =
                    new Exam(startDateTime.toDate(), startDateTime.toDate(), endDateTime.toDate(), executionCourses,
                            degreeModuleScopes, rooms, null, season);
            createWrittenEvaluationEnrolmentPeriodAndVigilancies(executionPeriod, exam, executionCourse);
        }
    }

    @Atomic(mode = TxMode.READ)
    private static void doIt() {
        setPrivledges();
        //createTestData();
    }

    private static final LessonRoomManager lessonRoomManager = new LessonRoomManager();
    private static final ExamRoomManager examRoomManager = new ExamRoomManager();
    private static final WrittenTestsRoomManager writtenTestsRoomManager = new WrittenTestsRoomManager();

    private static Teacher createTeachers(final int i) {
        final Person person = createPerson("Guru Diplomado", "teacher", i);
        new Employee(person, Integer.valueOf(i));
        final Teacher teacher = new Teacher(person);
//        person.addPersonRoleByRoleType(RoleType.EMPLOYEE);
//        person.addPersonRoleByRoleType(RoleType.TEACHER);
        // final Login login = Login.readUserLoginIdentification(person.getUser());
        // login.openLoginIfNecessary(RoleType.TEACHER);
        new EmployeeContract(person, new YearMonthDay().minusYears(2), new YearMonthDay().plusYears(2), Bennu.getInstance()
                .getInstitutionUnit(), AccountabilityTypeEnum.WORKING_CONTRACT, true);
        new EmployeeContract(person, new YearMonthDay().minusYears(2), new YearMonthDay().plusYears(2), Bennu.getInstance()
                .getInstitutionUnit(), AccountabilityTypeEnum.MAILING_CONTRACT, true);
//        person.addPersonRoleByRoleType(RoleType.ACADEMIC_ADMINISTRATIVE_OFFICE);
//        person.addPersonRoleByRoleType(RoleType.RESOURCE_ALLOCATION_MANAGER);
//        person.addPersonRoleByRoleType(RoleType.DEGREE_ADMINISTRATIVE_OFFICE);
//        person.addPersonRoleByRoleType(RoleType.DEGREE_ADMINISTRATIVE_OFFICE_SUPER_USER);
        /*
         * final Vigilant vigilant = new Vigilant(person,
         * ExecutionYear.readCurrentExecutionYear());
         */
        final VigilantGroup vigilantGroup;
        if (Bennu.getInstance().getVigilantGroupsSet().isEmpty()) {
            vigilantGroup = new VigilantGroup();
            vigilantGroup.setName("Officers of the Law");
            vigilantGroup.setUnit(Bennu.getInstance().getInstitutionUnit());
            vigilantGroup.setContactEmail("nowhere@nowhere.com");
            vigilantGroup.setRulesLink("http://www.google.com/");
            ExecutionYear currentYear = ExecutionYear.readCurrentExecutionYear();
            vigilantGroup.setExecutionYear(currentYear);
        } else {
            vigilantGroup = Bennu.getInstance().getVigilantGroupsSet().iterator().next();
        }
        /* vigilantGroup.addVigilants(vigilant); */
        return teacher;
    }

    private static Person createPerson(final String namePrefix, final String usernamePrefix, final int i) {
//        final Login login = Login.readUserLoginIdentification(user);
//        login.setPassword(PasswordEncryptor.encryptPassword("pass"));
//        login.setActive(Boolean.TRUE);
//        LoginAlias.createNewCustomLoginAlias(login, usernamePrefix + i);
        return null;
    }

    private static void createStudents() {
        final Bennu rootDomainObject = Bennu.getInstance();
        int i = 1;
        for (final DegreeCurricularPlan degreeCurricularPlan : DegreeCurricularPlan.readNotEmptyDegreeCurricularPlans()) {
            for (int k = 1; k <= 20; k++) {
                createStudent(degreeCurricularPlan, i++);
            }
        }
    }

    private static void createStudent(final DegreeCurricularPlan degreeCurricularPlan, final int i) {
        final Person person = createPerson("Esponja de Informacao", "student", i);
        final Student student = new Student(person, Integer.valueOf(i));
        // final Registration registration = new Registration(person,
        // degreeCurricularPlan);
        final Registration registration = new Registration(person, Integer.valueOf(i), degreeCurricularPlan.getDegree());
        registration.setDegree(degreeCurricularPlan.getDegree());
        registration.setStudent(student);
        final StudentCurricularPlan studentCurricularPlan =
                StudentCurricularPlan.createWithEmptyStructure(registration, degreeCurricularPlan,
                        new YearMonthDay().minusMonths(6));
//        person.addPersonRoleByRoleType(RoleType.STUDENT);
        // final Login login = Login.readUserLoginIdentification(person.getUser());
        // login.openLoginIfNecessary(RoleType.STUDENT);
        createStudentEnrolments(studentCurricularPlan);
        new DegreeCurricularPlanServiceAgreement(student.getPerson(), degreeCurricularPlan.getServiceAgreementTemplate());
        final ExecutionYear executionYear = ExecutionYear.readCurrentExecutionYear();
        final AdministrativeOffice administrativeOffice =
                AdministrativeOffice.readByAdministrativeOfficeType(studentCurricularPlan.getDegreeType()
                        .getAdministrativeOfficeType());
        new GratuityEventWithPaymentPlan(administrativeOffice, student.getPerson(), studentCurricularPlan, executionYear);
        new AdministrativeOfficeFeeAndInsuranceEvent(administrativeOffice, student.getPerson(), executionYear);
        // new InsuranceEvent(student.getPerson(), executionYear);
    }

    private static ExecutionSemester findFirstExecutionPeriod() {
        for (final ExecutionSemester executionPeriod : Bennu.getInstance().getExecutionPeriodsSet()) {
            if (executionPeriod.getPreviousExecutionPeriod() == null) {
                return executionPeriod;
            }
        }
        return null;
    }

    private static void createDegrees(AdminUserSection adminSection) {
        final Person person = Person.findByUsername(adminSection.getAdminUsername());

        final ExecutionYear executionYear = ExecutionYear.readCurrentExecutionYear();
        final Space campus = getCampus();

        int i = 0;
        for (final DegreeType degreeType : DegreeType.NOT_EMPTY_VALUES) {
            ++i;
            final GradeScale gradeScale = degreeType.getGradeScale();
            final Degree degree;
            final DegreeCurricularPlan degreeCurricularPlan;
            if (degreeType.isBolonhaType()) {
                degree =
                        new Degree("Agricultura do Conhecimento", "Knowledge Agriculture", "CODE" + i, degreeType, 0d,
                                gradeScale, null, Bennu.getInstance().getAdministrativeOfficesSet().iterator().next());
                degreeCurricularPlan = degree.createBolonhaDegreeCurricularPlan("DegreeCurricularPlanName", gradeScale, person);
                degreeCurricularPlan.setCurricularStage(CurricularStage.PUBLISHED);
            } else {
                degree = new Degree("Agricultura do Conhecimento", "Knowledge Agriculture", "CODE" + i, degreeType, gradeScale);
                degreeCurricularPlan =
                        degree.createPreBolonhaDegreeCurricularPlan("DegreeCurricularPlanName", DegreeCurricularPlanState.ACTIVE,
                                new Date(), null, degreeType.getYears(), Integer.valueOf(1),
                                Double.valueOf(degree.getEctsCredits()), MarkType.TYPE20_OBJ, Integer.valueOf(100), null,
                                gradeScale);
                final Branch branch = new Branch("", "", "", degreeCurricularPlan);
                branch.setBranchType(BranchType.COMNBR);
                createPreBolonhaCurricularCourses(degreeCurricularPlan, i, executionYear, branch);
            }

            final Department department = new Department();
            department.setCode(degree.getSigla());
            department.setCompetenceCourseMembersGroup(RoleType.TEACHER.actualGroup());

            department.setName("Department " + degree.getName());
            department.setRealName("Department " + degree.getName());
            // final DepartmentUnit departmentUnit = createDepartmentUnut(3020 +
            // i, department);
            // department.setDepartmentUnit(departmentUnit);
            department.addDegrees(degree);

            // createNewDegreeUnit(4020 + i, degree);

            // createDegreeInfo(degree);
            degreeCurricularPlan.setDescription("Bla bla bla. Descricao do plano curricular do curso. Bla bla bla");
            degreeCurricularPlan.setDescriptionEn("Blur ble bla. Description of the degrees curricular plan. Goo goo foo foo.");

            final ExecutionDegree executionDegree =
                    degreeCurricularPlan.createExecutionDegree(executionYear, campus, Boolean.FALSE);
            final Teacher teacher = createTeachers(i);
            Coordinator.createCoordinator(executionDegree, teacher.getPerson(), Boolean.TRUE);
            // createPeriodsForExecutionDegree(executionDegree);
            createSchoolClasses(executionDegree);
            // createEnrolmentPeriods(degreeCurricularPlan, executionYear);
            executionDegree.setCampus(getCampus());

            createAgreementsAndPostingRules(executionYear, degreeCurricularPlan);
        }
    }

    private static void createAgreementsAndPostingRules(final ExecutionYear executionYear,
            final DegreeCurricularPlan degreeCurricularPlan) {
        new DegreeCurricularPlanServiceAgreementTemplate(degreeCurricularPlan);

        final GratuityPaymentPlan gratuityPaymentPlan =
                new FullGratuityPaymentPlan(executionYear, degreeCurricularPlan.getServiceAgreementTemplate(), true);

        new InstallmentWithMonthlyPenalty(gratuityPaymentPlan, new Money("350"), executionYear.getBeginDateYearMonthDay(),
                new YearMonthDay(2006, 12, 15), new BigDecimal("0.01"), new YearMonthDay(2007, 1, 1), 9);

        new InstallmentWithMonthlyPenalty(gratuityPaymentPlan, new Money("570.17"), new YearMonthDay(2006, 12, 16),
                new YearMonthDay(2007, 5, 31), new BigDecimal("0.01"), new YearMonthDay(2007, 6, 1), 4);

        final GratuityPaymentPlan gratuityPaymentPlanForStudentsEnroledOnlyInSecondSemester =
                new GratuityPaymentPlanForStudentsEnroledOnlyInSecondSemester(executionYear,
                        degreeCurricularPlan.getServiceAgreementTemplate());

        new InstallmentWithMonthlyPenalty(gratuityPaymentPlanForStudentsEnroledOnlyInSecondSemester, new Money("920.17"),
                executionYear.getBeginDateYearMonthDay(), new YearMonthDay(2007, 5, 31), new BigDecimal("0.01"),
                new YearMonthDay(2007, 06, 1), 4);

        new GratuityWithPaymentPlanPR(EntryType.GRATUITY_FEE, EventType.GRATUITY, new DateTime(), null,
                degreeCurricularPlan.getServiceAgreementTemplate());
    }

    private static void createSchoolClasses(final ExecutionDegree executionDegree) {
        final ExecutionYear executionYear = executionDegree.getExecutionYear();
        final Degree degree = executionDegree.getDegree();
        final DegreeType degreeType = degree.getDegreeType();
        for (final ExecutionSemester executionPeriod : executionYear.getExecutionPeriodsSet()) {
            for (int y = 1; y <= degreeType.getYears(); y++) {
                for (int i = 1; i <= 3; i++) {
                    final String name = degreeType.isBolonhaType() ? Integer.toString(i) : degree.getSigla() + y + i;
                    new SchoolClass(executionDegree, executionPeriod, name, Integer.valueOf(y));
                }
            }
        }
    }

    private static void createExecutionCourses() {
        for (final DegreeModule degreeModule : Bennu.getInstance().getDegreeModulesSet()) {
            if (degreeModule instanceof CurricularCourse) {
                final CurricularCourse curricularCourse = (CurricularCourse) degreeModule;
                for (final ExecutionSemester executionPeriod : Bennu.getInstance().getExecutionPeriodsSet()) {
                    if (!curricularCourse.getActiveDegreeModuleScopesInExecutionPeriod(executionPeriod).isEmpty()) {
                        createExecutionCourse(executionPeriod, curricularCourse);
                    }
                }
            }
        }
    }

    private static void createExecutionCourse(final ExecutionSemester executionPeriod, final CurricularCourse curricularCourse) {
        final ExecutionCourse executionCourse =
                new ExecutionCourse(curricularCourse.getName(), curricularCourse.getAcronym(), executionPeriod, null);
        executionCourse.addAssociatedCurricularCourses(curricularCourse);

        createProfessorship(executionCourse, Boolean.TRUE);
        createProfessorship(executionCourse, Boolean.FALSE);
        // createAnnouncementsAndPlanning(executionCourse);
        // createShifts(executionCourse);
        // createEvaluationMethod(executionCourse);
        // createBibliographicReferences(executionCourse);
        createShiftProfessorhips(executionCourse);
    }

    private static void createShiftProfessorhips(final ExecutionCourse executionCourse) {
        final ExecutionSemester executionPeriod = ExecutionSemester.readActualExecutionSemester();
        for (final Professorship professorship : executionCourse.getProfessorshipsSet()) {
            final Teacher teacher = professorship.getTeacher();
            for (final Shift shift : executionCourse.getAssociatedShifts()) {
                if ((professorship.isResponsibleFor() && shift.containsType(ShiftType.TEORICA))
                        || (!professorship.isResponsibleFor() && !shift.containsType(ShiftType.TEORICA))) {

                    final ShiftProfessorship shiftProfessorship = new ShiftProfessorship();
                    shiftProfessorship.setShift(shift);
                    shiftProfessorship.setProfessorship(professorship);
                    shiftProfessorship.setPercentage(Double.valueOf(100));

                    TeacherService teacherService = TeacherService.getTeacherServiceByExecutionPeriod(teacher, executionPeriod);
                    if (teacherService == null) {
                        teacherService = new TeacherService(teacher, executionPeriod);
                    }
                    // final DegreeTeachingService degreeTeachingService = new
                    // DegreeTeachingService(teacherService, professorship,
                    // shift, Double.valueOf(100), RoleType.SCIENTIFIC_COUNCIL);

                    final SupportLessonDTO supportLessonDTO = new SupportLessonDTO();
                    supportLessonDTO.setProfessorshipID(professorship.getExternalId());
                    supportLessonDTO.setPlace("Room23");
                    supportLessonDTO.setStartTime(new DateTime().withField(DateTimeFieldType.hourOfDay(), 20).toDate());
                    supportLessonDTO.setEndTime(new DateTime().withField(DateTimeFieldType.hourOfDay(), 21).toDate());
                    supportLessonDTO.setWeekDay(new DiaSemana(DiaSemana.SABADO));

                    SupportLesson.create(supportLessonDTO, professorship, RoleType.SCIENTIFIC_COUNCIL);
                }
            }
        }
    }

    private static Calendar toCalendar(final HourMinuteSecond hourMinuteSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourMinuteSecond.getHour());
        calendar.set(Calendar.MINUTE, hourMinuteSecond.getMinuteOfHour());
        calendar.set(Calendar.SECOND, hourMinuteSecond.getSecondOfMinute());
        return calendar;
    }

    private static void createPreBolonhaCurricularCourses(final DegreeCurricularPlan degreeCurricularPlan, int dcpCounter,
            final ExecutionYear executionYear, final Branch branch) {
        for (final CurricularSemester curricularSemester : Bennu.getInstance().getCurricularSemestersSet()) {
            final CurricularYear curricularYear = curricularSemester.getCurricularYear();
            for (int i = 1; i < 6; i++) {
                final String x = "" + dcpCounter + i + curricularYear.getYear() + curricularSemester.getSemester();
                final CurricularCourse curricularCourse =
                        degreeCurricularPlan.createCurricularCourse("Germinacao do Conhecimento" + x, "C" + x, "D" + x,
                                Boolean.TRUE, CurricularStage.OLD);
                curricularCourse.setNameEn("KnowledgeGermination" + x);
                curricularCourse.setType(CurricularCourseType.NORMAL_COURSE);
                curricularCourse.setTheoreticalHours(Double.valueOf(3d));
                curricularCourse.setPraticalHours(Double.valueOf(2d));
                curricularCourse.setMinimumValueForAcumulatedEnrollments(Integer.valueOf(1));
                curricularCourse.setMaximumValueForAcumulatedEnrollments(Integer.valueOf(2));
                curricularCourse.setWeigth(Double.valueOf(6));
                new CurricularCourseScope(branch, curricularCourse, curricularSemester, executionYear.getBeginDateYearMonthDay()
                        .toDateMidnight().toCalendar(null), null, null);
                final Curriculum curriculum = new Curriculum();
                curriculum.setCurricularCourse(curricularCourse);
                curriculum.setGeneralObjectives("Objectivos gerais bla bla bla bla bla.");
                curriculum.setGeneralObjectivesEn("General objectives blur ble ble blur.");
                curriculum.setOperacionalObjectives("Objectivos Operacionais bla bla bla bla bla.");
                curriculum.setOperacionalObjectivesEn("Operational objectives blur ble ble blur.");
                curriculum.setProgram("Programa bla bla bla bla bla.");
                curriculum.setProgramEn("Program blur ble ble blur.");
                curriculum.setLastModificationDateDateTime(new DateTime());
                final CompetenceCourse competenceCourse =
                        new CompetenceCourse(curricularCourse.getCode(), curricularCourse.getName(), null);
                curricularCourse.setCompetenceCourse(competenceCourse);
            }
        }
    }

    private static void createProfessorship(final ExecutionCourse executionCourse, final Boolean isResponsibleFor) {
        final int n = Bennu.getInstance().getTeachersSet().size();
        final Teacher teacher = createTeachers(n + 1);
        final Professorship professorship = new Professorship();
        professorship.setTeacher(teacher);
        professorship.setExecutionCourse(executionCourse);
        professorship.setResponsibleFor(isResponsibleFor);
    }

    private static void connectShiftsToSchoolClasses() {
        final Bennu rootDomainObject = Bennu.getInstance();
        for (final CurricularCourseScope curricularCourseScope : rootDomainObject.getCurricularCourseScopesSet()) {
            final CurricularSemester curricularSemester = curricularCourseScope.getCurricularSemester();
            final CurricularYear curricularYear = curricularSemester.getCurricularYear();
            final CurricularCourse curricularCourse = curricularCourseScope.getCurricularCourse();
            final DegreeCurricularPlan degreeCurricularPlan = curricularCourse.getDegreeCurricularPlan();
            final ExecutionCourse executionCourse = curricularCourse.getAssociatedExecutionCoursesSet().iterator().next();
            final ExecutionDegree executionDegree = degreeCurricularPlan.getExecutionDegreesSet().iterator().next();
            for (final Shift shift : executionCourse.getAssociatedShifts()) {
                for (final SchoolClass schoolClass : executionDegree.getSchoolClassesSet()) {
                    if (schoolClass.getExecutionPeriod() == executionCourse.getExecutionPeriod()
                            && schoolClass.getAnoCurricular().intValue() == curricularYear.getYear().intValue()) {
                        schoolClass.addAssociatedShifts(shift);
                    }
                }
            }
        }
        // TODO : do the same for bolonha structure.
    }

    private static void createWrittenEvaluations() {
        final Bennu rootDomainObject = Bennu.getInstance();
        for (final ExecutionSemester executionPeriod : rootDomainObject.getExecutionPeriodsSet()) {
            createWrittenEvaluations(executionPeriod, new Season(Season.SEASON1), "Teste1");
            for (int i = 0; i++ < 500; writtenTestsRoomManager.getNextDateTime(executionPeriod)) {
                ;
            }
            createWrittenEvaluations(executionPeriod, new Season(Season.SEASON2), "Teste2");
        }
    }

    private static void createWrittenEvaluations(final ExecutionSemester executionPeriod, final Season season,
            final String writtenTestName) {
        for (final ExecutionCourse executionCourse : executionPeriod.getAssociatedExecutionCoursesSet()) {
            createWrittenEvaluation(executionPeriod, executionCourse, writtenTestName);
            createExam(executionPeriod, executionCourse, season);
        }
    }

    private static void createWrittenEvaluation(final ExecutionSemester executionPeriod, final ExecutionCourse executionCourse,
            final String name) {
        final DateTime startDateTime = writtenTestsRoomManager.getNextDateTime(executionPeriod);
        final DateTime endDateTime = startDateTime.plusMinutes(120);
        // final OldRoom oldRoom =
        // writtenTestsRoomManager.getNextOldRoom(executionPeriod);
        final List<ExecutionCourse> executionCourses = new ArrayList<ExecutionCourse>();
        executionCourses.add(executionCourse);
        final List<DegreeModuleScope> degreeModuleScopes = new ArrayList<DegreeModuleScope>();
        for (final CurricularCourse curricularCourse : executionCourse.getAssociatedCurricularCoursesSet()) {
            degreeModuleScopes.addAll(curricularCourse.getDegreeModuleScopes());
        }
        // final List<OldRoom> oldRooms = new ArrayList<OldRoom>();
        // oldRooms.add(oldRoom);
        final OccupationPeriod occupationPeriod =
                new OccupationPeriod(startDateTime.toYearMonthDay(), endDateTime.toYearMonthDay());
        // final WrittenTest writtenTest = new
        // WrittenTest(startDateTime.toDate(), startDateTime.toDate(),
        // endDateTime.toDate(), executionCourses, degreeModuleScopes, oldRooms,
        // occupationPeriod, name);
        // createWrittenEvaluationEnrolmentPeriodAndVigilancies(executionPeriod,
        // writtenTest, executionCourse);
    }

    private static void createExam(final ExecutionSemester executionPeriod, final ExecutionCourse executionCourse,
            final Season season) {
        final DateTime startDateTime = examRoomManager.getNextDateTime(executionPeriod);
        final DateTime endDateTime = startDateTime.plusMinutes(180);
        // final OldRoom oldRoom =
        // examRoomManager.getNextOldRoom(executionPeriod);
        final List<ExecutionCourse> executionCourses = new ArrayList<ExecutionCourse>();
        executionCourses.add(executionCourse);
        final List<DegreeModuleScope> degreeModuleScopes = new ArrayList<DegreeModuleScope>();
        for (final CurricularCourse curricularCourse : executionCourse.getAssociatedCurricularCoursesSet()) {
            degreeModuleScopes.addAll(curricularCourse.getDegreeModuleScopes());
        }
        // final List<OldRoom> oldRooms = new ArrayList<OldRoom>();
        // oldRooms.add(oldRoom);
        final OccupationPeriod occupationPeriod =
                new OccupationPeriod(startDateTime.toYearMonthDay(), endDateTime.toYearMonthDay());
        // final Exam exam = new Exam(startDateTime.toDate(),
        // startDateTime.toDate(), endDateTime.toDate(), executionCourses,
        // degreeModuleScopes, oldRooms, occupationPeriod, season);
        // createWrittenEvaluationEnrolmentPeriodAndVigilancies(executionPeriod,
        // exam, executionCourse);
    }

    private static void createWrittenEvaluationEnrolmentPeriodAndVigilancies(final ExecutionSemester executionPeriod,
            final WrittenEvaluation writtenEvaluation, final ExecutionCourse executionCourse) {
        writtenEvaluation.setEnrollmentBeginDayDateYearMonthDay(executionPeriod.getBeginDateYearMonthDay());
        writtenEvaluation.setEnrollmentBeginTimeDateHourMinuteSecond(new HourMinuteSecond(0, 0, 0));
        writtenEvaluation.setEnrollmentEndDayDateYearMonthDay(writtenEvaluation.getDayDateYearMonthDay().minusDays(1));
        writtenEvaluation.setEnrollmentEndTimeDateHourMinuteSecond(new HourMinuteSecond(0, 0, 0));

        final YearMonthDay yearMonthDay = writtenEvaluation.getDayDateYearMonthDay();
        writtenEvaluation.setDayDateYearMonthDay(new YearMonthDay().plusDays(1));
        final Vigilancy vigilancy = null;
        writtenEvaluation.setDayDateYearMonthDay(yearMonthDay);
        for (final Professorship professorship : executionCourse.getProfessorshipsSet()) {
            /*
             * final Vigilant vigilant = professorship.getPerson().
             * getVigilantForGivenExecutionYear(
             * executionPeriod.getExecutionYear());
             * 
             * vigilant.addVigilancies(vigilancy);
             */
        }
    }

    private static String constructExecutionYearString() {
        final YearMonthDay yearMonthDay = new YearMonthDay();
        return yearMonthDay.getMonthOfYear() < 8 ? constructExecutionYearString(yearMonthDay.minusYears(1), yearMonthDay) : constructExecutionYearString(
                yearMonthDay, yearMonthDay.plusYears(1));
    }

    private static String constructExecutionYearString(final YearMonthDay year1, final YearMonthDay year2) {
        return year1.toString("yyyy") + "/" + year2.toString("yyyy");
    }

    private static void createStudentEnrolments(final StudentCurricularPlan studentCurricularPlan) {
        final ExecutionSemester executionPeriod = ExecutionSemester.readActualExecutionSemester();
        if (studentCurricularPlan.isBolonhaDegree()) {

        } else {
            final DegreeCurricularPlan degreeCurricularPlan = studentCurricularPlan.getDegreeCurricularPlan();
            for (final DegreeModuleScope degreeModuleScope : degreeCurricularPlan.getDegreeModuleScopes()) {
                if (degreeModuleScope.getCurricularYear().intValue() == 1
                        && degreeModuleScope.getCurricularSemester() == executionPeriod.getSemester().intValue()) {
                    if (degreeModuleScope.isActiveForExecutionPeriod(executionPeriod)) {
                        final Enrolment enrolment =
                                new Enrolment(studentCurricularPlan, degreeModuleScope.getCurricularCourse(), executionPeriod,
                                        EnrollmentCondition.FINAL, null);
                        final Attends attends = getAttendsFor(enrolment, executionPeriod);
                        createStudentShifts(attends);
                    }
                }
            }
        }
    }

    private static Attends getAttendsFor(final Enrolment enrolment, final ExecutionSemester executionPeriod) {
        for (final Attends attends : enrolment.getAttendsSet()) {
            if (attends.getExecutionCourse().getExecutionPeriod() == executionPeriod) {
                return attends;
            }
        }
        return null;
    }

    private static void createStudentShifts(final Attends attends) {
        final ExecutionCourse executionCourse = attends.getExecutionCourse();
        for (final Shift shift : executionCourse.getAssociatedShifts()) {
            if (!isEnroledInShift(attends, shift.getTypes())) {
                shift.reserveForStudent(attends.getRegistration());
            }
        }
    }

    private static boolean isEnroledInShift(final Attends attends, final List<ShiftType> shiftTypes) {
        final ExecutionCourse executionCourse = attends.getExecutionCourse();
        for (final Shift shift : attends.getRegistration().getShiftsSet()) {
            if (shift.getTypes().containsAll(shiftTypes) && shift.getExecutionCourse() == executionCourse) {
                return true;
            }
        }
        return false;
    }

}
