/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;

@Named(value = "headerManagedBean")
@ViewScoped
public class headerManagedBean implements Serializable {

    public headerManagedBean() {
    }
    
}
