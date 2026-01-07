# University Management System (UMS)

A Production-Grade JavaFX application for managing university data — students, faculties, courses, enrollments, events, grades, and tuition.

**License:** No license specified · **Status:** Active development

---

## 🎯 What is UMS?

UMS (University Management System) is a desktop application designed for administrators, faculty, and staff to manage academic data efficiently. It provides a polished GUI built with JavaFX and a modular backend for importing/exporting records, running reports, and maintaining student and faculty information.

- 📚 Manage Students & Faculty — Add, edit, view, and delete records with validation
- 🧾 Courses & Enrollments — Create and manage course lists and student enrollments
- 📅 Events & Calendar — Create events with admin and user views
- 📊 Grades & Tuition — Record grades, compute tuition summaries, and generate reports
- 📤 Data Interchange — Import/export via CSV and Excel (Apache POI)

---

## ✨ Key Features

- JavaFX GUI with FXML-based controllers and responsive layouts
- Excel integration (read/write) using Apache POI
- Modular structure (JPMS) for clear encapsulation and maintainability
- Built-in CSV sample data (e.g., `registrations.csv`) and data validation
- Optional features: chatbots, Excel export for events, and reporting tools

---

## 🚀 Quick Start

### Requirements

- Java 17 (LTS) and a JDK installed
- Maven (project includes `mvnw` / `mvnw.cmd` wrappers)
- Platform note: `pom.xml` includes a JavaFX graphics classifier set to `win` by default — change to `linux` or `mac` when building on those platforms

### Run locally

From the project root:

Windows (using wrapper):

```bash
mvnw.cmd javafx:run
```

macOS / Linux:

```bash
./mvnw javafx:run
```

Or run the `HelloApplication` main class from your IDE.

### Build

```bash
./mvnw clean package
# Then run the jar if produced:
java -jar target/FinalProject-1.0-SNAPSHOT.jar
```

---

## 📁 Project Structure

```
mvnw
mvnw.cmd
pom.xml
README.md
registrations.csv
src/
  main/
    java/
      module-info.java
      com/
        FinalProject/
          UMS/
            AdminStudentController.java
            AZAdminaddstudent.java
            CalendarViewController.java
            CoursesController.java
            DashboardController.java
            EventAdminController.java
            EventController.java
            EventManagementExcel.java
            EventMenuController.java
            EventStudent.java
            EventUserController.java
            ExcelDatabase.java
            Faculties.java
            Faculty.java
            FacultyController.java
            FacultyDetailsController.java
            FacultyEditController.java
            FacultyExcelHandler.java
            FacultyListController.java
            FacultyViewController.java
            Grade.java
            GradesController.java
            HelloApplication.java
            HelloController.java
            LoginController.java
            MenuController.java
            ProfileController.java
            Student.java
            StudentDatabase.java
            StudentInfoController.java
            StudentManagementMenuController.java
            Subject.java
            SubjectManagementController.java
            Tuition.java
            TuitionController.java
            User.java
            WCourse.java
            WCourseController.java
            WCourseMenu.java
    resources/
      com/
        FinalProject/
          UMS/
            add-student-view.fxml
            admin-event-view.fxml
            admin-student-view.fxml
            calendar-view.fxml
            chatbot-popup.fxml
            CourseManagement-view.fxml
            courses-view.fxml
            dashboard-view.fxml
            edit-student-view.fxml
            event-menu-view.fxml
            event-user-view.fxml
            Faculty-add.fxml
            faculty-edit.fxml
            faculty-list.fxml
            faculty-view.fxml
            facultyDetailsPopup.fxml
            facultyStudentView.fxml
            grades-view.fxml
            hello-view.fxml
            login-view.fxml
            manage-enrollments-view.fxml
            ...
META-INF/
  MANIFEST.MF
```

(See `module-info.java` for JPMS module setup and required JavaFX modules.)

---

## 💻 Usage Examples

- Launch the app and use the GUI to add students, faculties, courses, and events.
- Use the Excel export/import features to move data between systems.

CLI automation (examples):

```bash
# Validate sample CSV (example script placeholder)
# ./scripts/validate_csv.sh registrations.csv

# Export student list to Excel via a provided utility class (run from IDE)
```

> Note: Currently the project is GUI-first. If you want a CLI or headless mode added, I can add a small command-line entry point.

---

## 🧪 Tests

Run unit tests with:

```bash
./mvnw test
```

---

## 📤 Outputs & Data Files

- `registrations.csv` — sample input data
- Excel exports for events, faculties, and students (via Apache POI)
- Logs and reports in project output folders when exported from the app

---

## 🧩 Contributing

Contributions are welcome — please open an issue to discuss significant changes before submitting a PR.

Suggested workflow:

```bash
# Fork the repo
git checkout -b feature/awesome-feature
git commit -m "Add awesome feature"
git push origin feature/awesome-feature
# Open a Pull Request
```

---

## 📝 License

No license specified. Add a `LICENSE` file to declare reuse permissions (e.g., MIT, Apache-2.0).

---

## 🙏 Acknowledgments

Built with JavaFX, Apache POI, ControlsFX and other rich-client libraries.

---

If you'd like, I can now add badges (build / license / Java version), screenshots of the UI, or platform-specific build notes — tell me which and I'll update `README.md`. ✅ 