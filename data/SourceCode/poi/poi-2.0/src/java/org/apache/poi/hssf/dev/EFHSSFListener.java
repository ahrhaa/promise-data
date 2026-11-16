package org.apache.poi.hssf.dev;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.eventmodel.*;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.usermodel.*;

class EFHSSFListener
    implements HSSFListener
{
    EFHSSF efhssf;

    public EFHSSFListener(EFHSSF efhssf)
    {
        this.efhssf = efhssf;
    }

    public void processRecord(Record record)
    {
        efhssf.recordHandler(record);
    }
}
