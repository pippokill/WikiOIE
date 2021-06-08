/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.udp;

/**
 *
 * @author pierpaolo
 */
public class UDPResult {

    private String model;

    private String[] acknowledgements;

    private String result;

    /**
     *
     */
    public UDPResult() {
    }

    /**
     *
     * @param model
     * @param acknowledgements
     * @param result
     */
    public UDPResult(String model, String[] acknowledgements, String result) {
        this.model = model;
        this.acknowledgements = acknowledgements;
        this.result = result;
    }

    /**
     *
     * @return
     */
    public String getModel() {
        return model;
    }

    /**
     *
     * @param model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     *
     * @return
     */
    public String[] getAcknowledgements() {
        return acknowledgements;
    }

    /**
     *
     * @param acknowledgements
     */
    public void setAcknowledgements(String[] acknowledgements) {
        this.acknowledgements = acknowledgements;
    }

    /**
     *
     * @return
     */
    public String getResult() {
        return result;
    }

    /**
     *
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return result;
    }

}
