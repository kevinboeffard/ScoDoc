package model.entity;

/**
 * Represents the relationship between a teacher and a module they teach.
 * 
 * @author Kevin Boeffard
 */
public class ACommeModule {

    /** The teacher. */
    private Enseignant enseignant;

    /** The module. */
    private Module module;

    /**
     * Creates a new ACommeModule with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is null.
     *
     * @param enseignant the teacher
     * @param module     the module
     */
    public ACommeModule(Enseignant enseignant, Module module) {
        if(enseignant == null) throw new IllegalArgumentException("L'enseignant ne peut pas être null");
        if(module == null) throw new IllegalArgumentException("Le module ne peut pas être null");
        this.enseignant = enseignant;
        this.module = module;
    }

    /**
     * Returns the teacher.
     * @return the teacher
     */
    public Enseignant getEnseignant() { return this.enseignant; }

    /**
     * Returns the module.
     * @return the module
     */
    public Module getModule() { return this.module; }
}