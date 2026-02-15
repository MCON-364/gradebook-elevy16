package edu.course.gradebook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GradebookTest {
    private Gradebook gradebook;

    @BeforeEach
    public void setup() {
        gradebook = new Gradebook();
    }

    @Test
    public void  addStudentTest() {
        // Act - add a new student
        boolean result = gradebook.addStudent("Sara");

        // Assert - should return true
        assertTrue(result, "Adding a new student should return true.");

        // Act - try adding same student again
        boolean result2 = gradebook.addStudent("Sara");

        // Assert - should return false
        assertFalse(result2, "Adding a duplicate student should return false.");
    }

    @Test
    public void addGradeTest() {
        // Arrange - add a student
        gradebook.addStudent("Sara");

        // Act - add a grade for Sara
        boolean result = gradebook.addGrade("Sara", 97);

        // Assert - should return true
        assertTrue(result, "Adding a new grade should return true.");

        // Act - try adding grade to non-existent student
        boolean result2 = gradebook.addGrade("Rochel", 67);

        // Assert - should return false
        assertFalse(result2, "Adding a grade to a non-existent student should return false.");
    }

    @Test
    public void  removeStudentTest() {
        // Arrange - add a new student
        gradebook.addStudent("Sara");

        // Act - remove Sara
        boolean result = gradebook.removeStudent("Sara");

        // Assert - should return true
        assertTrue(result, "Removing student should return true.");

        // Act - removing non-existent student should return false
        boolean result2 = gradebook.removeStudent("Rochel");

        // Assert - should return false
        assertFalse(result2, "Removing a non-existent student should return false.");
    }

    @Test
    public void averageForTest() {
        // Arrange - add Sara and grades
        gradebook.addStudent("Sara");
        gradebook.addGrade("Sara", 97);
        gradebook.addGrade("Sara", 88);
        gradebook.addGrade("Sara", 91);
        gradebook.addGrade("Sara", 79);
        gradebook.addGrade("Sara", 93);

        // Arrange - add Rochel with no grades
        gradebook.addStudent("Rochel");

        // Act - calculate average for Sara
        Optional<Double> result =  gradebook.averageFor("Sara");

        // Assert - average should be present and return 89.6
        assertTrue(result.isPresent());
        assertEquals(Optional.of(89.6), result, "Average should be 89.6");

        // Act - try calculating average for Rochel
        Optional<Double>  result2 = gradebook.averageFor("Rochel");

        // Assert - should be empty
        assertTrue(result2.isEmpty(), "Student with no grades should have empty average.");

        // Act - try calculating average for non-existent student
        Optional<Double>  result3 = gradebook.averageFor("Tova");

        // Assert - should be empty
        assertTrue(result3.isEmpty(), "non-existent student should have empty average.");
    }

    @Test
    public void letterGradeForTest() {
        // Arrange - add Sara with grades and average
        gradebook.addStudent("Sara");
        gradebook.addGrade("Sara", 97);
        gradebook.addGrade("Sara", 88);
        gradebook.addGrade("Sara", 91);
        gradebook.averageFor("Sara");

        // Arrange - add Rochel with no grades
        gradebook.addStudent("Rochel");

        // Act - get letter grade for Sara
        Optional<String> result = gradebook.letterGradeFor("Sara");

        // Assert - should be present and return A
        assertTrue(result.isPresent());
        assertEquals(Optional.of("A"), result, "Letter grade should be A");

        // Act - try getting letter grade for Rochel
        Optional<String> result2 = gradebook.letterGradeFor("Rochel");

        // Assert - should be empty
        assertTrue(result2.isEmpty(), "Student with no grades should have no letter grade.");

        // Act - try getting letter grade for non-existent student
        Optional<String> result3 = gradebook.letterGradeFor("Tova");

        // Assert - should be empty
        assertTrue(result3.isEmpty(), "Non-existent student should have no letter grade.");
    }

    @Test
    public void classAverageTest() {
        // Arrange - add students and grades
        gradebook.addStudent("Sara");
        gradebook.addGrade("Sara", 97);
        gradebook.addGrade("Sara", 99);
        gradebook.addGrade("Sara", 100);

        gradebook.addStudent("Rochel");
        gradebook.addGrade("Rochel", 83);
        gradebook.addGrade("Rochel", 88);
        gradebook.addGrade("Rochel", 79);

        gradebook.addStudent("Tova");
        gradebook.addGrade("Tova", 69);
        gradebook.addGrade("Tova", 72);
        gradebook.addGrade("Tova", 69);

        // Act - get class average
        Optional<Double> result = gradebook.classAverage();

        // Assert - class average should be present and return 84
        assertTrue(result.isPresent());
        assertEquals(Optional.of(84.0), result, "Class average should return 84");

        // Arrange - remove students
        gradebook.removeStudent("Sara");
        gradebook.removeStudent("Rochel");
        gradebook.removeStudent("Tova");

        // Act - try getting class average for empty gradebook
        Optional<Double> result2 = gradebook.classAverage();

        // Assert - should be empty
        assertTrue(result2.isEmpty());
    }

    @Test
    public void  undoTest() {
        // Test 1: Undo addGrade
        // Arrange - add student and grade
        gradebook.addStudent("Sara");
        gradebook.addGrade("Sara", 97);

        // Act - undo the addGrade
        boolean result = gradebook.undo();

        // Assert - undo should succeed
        assertTrue(result, "Undo should return true.");
        // Assert - grade should be removed
        var gradesAfterUndo = gradebook.findStudentGrades("Sara");
        assertTrue(gradesAfterUndo.isPresent(), "Sara should still exist.");
        assertEquals(0, gradesAfterUndo.get().size(), "Sara should have 0 grades after undo");

        // Test2: Undo removeStudent
        // Arrange - add student with grades, then remove them
        gradebook.addStudent("Rochel");
        gradebook.addGrade("Rochel", 83);
        gradebook.addGrade("Rochel", 88);
        gradebook.removeStudent("Rochel");

        // Act - undo the removeStudent
        boolean result2 = gradebook.undo();

        // Assert - undo should succeed
        assertTrue(result2, "Undo should return true.");
        // Assert - Rochel should be restored with grades
        var rochelAfterUndo = gradebook.findStudentGrades("Rochel");
        assertTrue(rochelAfterUndo.isPresent(), "Rochel should be restored.");
        assertEquals(2, rochelAfterUndo.get().size(), "Rochel should have 2 grades.");

        // Test 3: Undo with empty stack
        // Arrange - undo twice to empty the stack
        gradebook.undo();
        gradebook.undo();

        // Act - try to undo when stack is empty
        boolean result3 = gradebook.undo();

        // Assert - should return false
        assertFalse(result3, "Undo on an empty stack should return false.");
    }

    @Test
    public void recentLogTest() {
        // Test 1: Get recent entries when log has items
        // Arrange - fill the log
        gradebook.addStudent("Sara");           // entry 1
        gradebook.addStudent("Rochel");         // entry 2
        gradebook.addGrade("Sara", 97);   // entry 3
        gradebook.addGrade("Sara", 88);   // entry 4
        gradebook.addGrade("Rochel", 83); // entry 5

        // Act - get last 3 entries
        List<String> result = gradebook.recentLog(3);

        // Assert - should get the last 3 entries
        assertEquals(3, result.size(), "Should return 3 entries.");
        assertEquals("Added grade 97 for Sara", result.get(0), "First should be grade for Sara" );
        assertEquals("Added grade 88 for Sara",  result.get(1), "Second should be grade for Sara" );
        assertEquals("Added grade 83 for Rochel", result.get(2), "Third should be grade for Rochel" );

        // Test 2: Ask for more items than exist
        // Act - ask for 10 items when only 5 exist
        List<String> result2 = gradebook.recentLog(10);

        // Assert - should return all 5 entries
        assertEquals(5, result2.size(), "Should return all 5 entries.");

        // Test 3: Get from empty log
        // Arrange - create empty gradebook
        Gradebook emptyGradebook = new Gradebook();

        // Act - try to get recent logs
        List<String> emptyResult = emptyGradebook.recentLog(10);

        // Assert - should be empty
        assertTrue(emptyResult.isEmpty(), "Empty gradebook should have empty log.");

    }

}
