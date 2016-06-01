package com.aleiacampo.oristanobus.util;
/**
 * Created by Ale on 02/11/2015.
 */

public class Stop {

    public int id;
    public int idLine;
    public int idStop;
    public String nameLine;
    public String nameStop;

    public Stop(){

    }

    public Stop(int idLine, int idStop, String nameLine, String nameStop){
        this.idStop = idStop;
        this.idLine = idLine;
        this.nameLine = nameLine;
        this.nameStop = nameStop;
    }
/*
    public void setLine(int idLine, String nameLine){
        this.idLine = idLine;
        this.nameLine = nameLine;
    }
    */

    public Stop(int idStop, String nameStop){
        this.idStop = idStop;
        this.nameStop = nameStop;
    }

}