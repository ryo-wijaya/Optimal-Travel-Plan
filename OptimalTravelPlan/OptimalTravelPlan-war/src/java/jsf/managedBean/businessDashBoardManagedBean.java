/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Jorda
 */
@Named(value = "businessDashBoardManagedBean")
@ViewScoped
public class businessDashBoardManagedBean implements Serializable {

    private PieChartModel model;
    
    /**
     * Creates a new instance of businessDashBoardManagedBean
     */
    public businessDashBoardManagedBean() {
    }
    
    public void init() {
        model = new PieChartModel();
        model.set("test1", 10);
        model.set("test2", 60);
        model.set("test3", 20);
        model.set("test4", 10);

        model.setTitle("2018 Jobs for top languages");
        //set legend position to 'e' (east), other values are 'w', 's' and 'n'
        model.setLegendPosition("e");
        //enable tooltips
        model.setShowDatatip(true);
        //show labels inside pie chart
        model.setShowDataLabels(true);
        //show label text  as 'value' (numeric) , others are 'label', 'percent' (default). Only one can be used.
        model.setDataFormat("value");
        //format: %d for 'value', %s for 'label', %d%% for 'percent'
        model.setDataLabelFormatString("%dK");
        //pie sector colors
        model.setSeriesColors("aaf,afa,faa,ffa,aff,faf,ddd");
    }
    
    public PieChartModel getModel() {
        return model;
    }
}
