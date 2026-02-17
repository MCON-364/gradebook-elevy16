package edu.course.gradebook;

import java.util.*;

public class Gradebook {

    private final Map<String, List<Integer>> gradesByStudent = new HashMap<>();
    private final Deque<UndoAction> undoStack = new ArrayDeque<>();
    private final LinkedList<String> activityLog = new LinkedList<>();

    public Optional<List<Integer>> findStudentGrades(String name) {
        return Optional.ofNullable(gradesByStudent.get(name));
    }

    /**
     * Adds a student to the gradebook
     * @param name the student's name
     * @return true if student was added, false if student already exists
     */
    public boolean addStudent(String name) {

        // check if student already exists
        if (gradesByStudent.containsKey(name)) {
            return false;
        }

        // add student to map
        gradesByStudent.put(name, new ArrayList<>());

        // log entry and return true
        activityLog.add("Added student " + name);
        return true;
    }

    /**
     * Adds grade to student grade list
     * @param name the student's name
     * @param grade the student's grade
     * @return true if grade added successfully, false if student doesn't exist
     */
    public boolean addGrade(String name, int grade) {
        // find student's grade list
        Optional<List<Integer>> studentGradesOptional = findStudentGrades(name);

        // return false if student doesn't exist
        if (studentGradesOptional.isEmpty()) {
            return false;
        }

        // add the grade to their list
        List<Integer> studentGrades = studentGradesOptional.get();
        studentGrades.add(grade);

        // push an undo action to undoStack to remove this grade when called
        undoStack.push((gradebook) ->
            studentGrades.remove(studentGrades.size() - 1));

        // log entry
        activityLog.add("Added grade " + grade + " for " + name);
        return true;
    }

    /**
     * Removes student from map
     * @param name the student's name
     * @return true if student removed successfully, false if student doesn't exist
     */
    public boolean removeStudent(String name) {
        // return false if student doesn't exist
        if (!gradesByStudent.containsKey(name)) {
            return false;
        }

        // store old grades in case of undo
        List<Integer> oldGrades =  gradesByStudent.get(name);

        // remove student from list
        gradesByStudent.remove(name);

        // push undo action to undo stack
        undoStack.push((gradebook) -> gradesByStudent.put(name, oldGrades));

        // log entry
        activityLog.add("Removed student " + name);
        return true;

    }

    /**
     * Calculates grade average for a student
     * @param name the student's name
     * @return Optional grade average
     */
    public Optional<Double> averageFor(String name) {
        // look up this student, get optional with their grades or empty
        var gradesOptional = findStudentGrades(name);

        // if student doesn't exist, don't calculate average
        if (gradesOptional.isEmpty()) {
            return Optional.empty();
        }

        // student exists, get their actual grade list
        var grades = gradesOptional.get();

        // if grades don't exist, don't calculate average
        if (grades.isEmpty()) {
            return Optional.empty();
        }

        // add grade
        int count = 0;
        int sum = 0;
        for (var grade : grades) {
            sum += grade;
            count++;
        }

        // calculate average and return
        double average = (double) sum / count;
        return Optional.of(average);
    }

    /**
     * Calculates letter grade for student
     * @param name the student's name
     * @return student's letter grade or empty, if student or grades don't exist
     */
    public Optional<String> letterGradeFor(String name) {
        // get average optional
        var averageOptional = averageFor(name);

        // check if empty
        if (averageOptional.isEmpty()) {
            return Optional.empty();
        }

        // get actual average
        double average =  averageOptional.get();

        // convert to tens digit to use in switch statement
        int grade = (int) average/10;

        // calculate letter grade
        String letter = switch (grade) {
            case 10, 9 -> {
                yield "A";
            }
            case 8 -> {
                yield "B";
            }
            case 7 -> {
                yield "C";
            }
            case 6 -> {
                yield "D";
            }
            default -> {
                yield "F";
            }
        };
        return Optional.of(letter);
    }

    /**
     * Calculates class average across all students
     * @return Optional class average, or empty if no grades exist
     */
    public Optional<Double> classAverage() {
        int gradeCount = 0;
        int gradeSum = 0;

        // calculate grade sum and count for this student
        for(var entry : gradesByStudent.entrySet()) {
            List<Integer> grades = entry.getValue();
            for(int grade : grades ) {
                gradeCount++;
                gradeSum += grade;
            }
        }

        // return empty if no grades
        if (gradeCount == 0) {
            return Optional.empty();
        }

        // calculate and return average
        double average = (double) gradeSum / gradeCount;
        return Optional.of(average);    }

    /**
     * Undoes last action
     * @return true if undo is successful, false if stack is empty
     */
    public boolean undo() {

        // if stack is empty, return false
        if (undoStack.isEmpty()) {
            return false;
        }

        // pop most recent action
        UndoAction action = undoStack.pop();

        // execute the undo
        action.undo(this);

        // log and return true
        activityLog.add("Undid last action");
        return true;
    }

    /**
     * Returns the most recent log entries
     * @param maxItems the max entries to return
     * @return list of recent logs
     */
    public List<String> recentLog(int maxItems) {

        // calculate where to start getting log entries
        int start = Math.max(0, activityLog.size() - maxItems);

        // get entries from start to end of log
        return activityLog.subList(start, activityLog.size());
    }
}
