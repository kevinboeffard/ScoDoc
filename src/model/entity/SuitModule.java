package model.entity;

/**
 * Represents the relationship between a tutorial group and a module they follow.
 * 
 * @author Kevin Boeffard
 */
public class SuitModule {

    /** The tutorial group. */
    private GroupeTD groupe;

    /** The module. */
    private Module module;

    /**
     * Creates a new SuitModule with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is null.
     *
     * @param groupe  the tutorial group
     * @param module  the module
     */
    public SuitModule(GroupeTD groupe, Module module) {
        if(groupe == null) throw new IllegalArgumentException("Le groupe ne peut pas être null");
        if(module == null) throw new IllegalArgumentException("Le module ne peut pas être null");
        this.groupe = groupe;
        this.module = module;
    }

    /**
     * Returns the tutorial group.
     * @return the tutorial group
     */
    public GroupeTD getGroupe() {
        return this.groupe;
    }

    /**
     * Returns the module.
     * @return the module
     */
    public Module getModule() {
        return this.module;
    }
}