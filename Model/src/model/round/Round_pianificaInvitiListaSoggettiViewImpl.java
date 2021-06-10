package model.round;

import model.round.common.Round_pianificaInvitiListaSoggettiView;

import oracle.jbo.Row;
import oracle.jbo.server.ViewObjectImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Thu Aug 06 10:09:21 CEST 2020
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Round_pianificaInvitiListaSoggettiViewImpl extends ViewObjectImpl implements Round_pianificaInvitiListaSoggettiView {
    /**
     * This is the default constructor (do not remove).
     */
    public Round_pianificaInvitiListaSoggettiViewImpl() {
    }
    
    @Override
    public void insertRow(Row row) {
        //go to the end of Rowset if it has rows
        Row lastRow = this.last();
        if (lastRow != null) {
            //insert new row at the end and make it current
            int indx = this.getRangeIndexOf(lastRow) + 1;
            this.insertRowAtRangeIndex(indx, row);
            this.setCurrentRow(row);
        } else { // empty Rowset
            super.insertRow(row);
        }
    }

}
