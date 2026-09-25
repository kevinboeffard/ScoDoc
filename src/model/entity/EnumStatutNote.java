package model.entity;

/**
 * Represents the status of a grade.
 * OK  : grade is valid.
 * ABS : student was absent (grade = 0).
 * EXC : student was excused (grade = NULL, neutralized).
 * ATT : grade is pending (grade = NULL).
 * 
 * @author Kevin Boeffard
 */
public enum EnumStatutNote {

    /** Grade is valid. */
    OK,

    /** Student was absent, grade is set to zero. */
    ABS,
    
    /** Student was excused, grade is neutralized. */
    EXC,
    
    /** Grade is pending. */
    ATT
}