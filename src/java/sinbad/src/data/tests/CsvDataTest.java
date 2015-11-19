package data.tests;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.Test;

import core.access.*;
import core.schema.*;
import core.util.IOUtil;
import data.csv.*;

public class CsvDataTest {

    @Test
    public void testSingleRow() {
        InputStream example = IOUtil.createInput("src/data/tests/example.csv");
        IDataAccess csv = new CsvFactory().newInstance(example);
        
        assertEquals("1997", csv.get(null, 0).get("Year").getContents());
        assertEquals("Venture \"Extended Edition, Very Large\"", csv.get(null, 3).get("Model").getContents());
        assertEquals("ac, abs, moon", csv.get(null, 0).get("Description").getContents());
        assertEquals("", csv.get(null, 1).get("Description").getContents());
    }
    
    @Test
    public void testGetAll() {
        InputStream example = IOUtil.createInput("src/data/tests/example.csv");
        IDataAccess csv = new CsvFactory().newInstance(example);
        Stream<IDataAccess> s = csv.getAll(null);
        IDataAccess[] rows = s.toArray(IDataAccess[]::new);
        
        assertEquals("1997", rows[0].get("Year").getContents());
        assertEquals("", rows[1].get("Description").getContents());
        assertEquals("Venture \"Extended Edition, Very Large\"", rows[3].get("Model").getContents());
    }

    @Test
    public void testGetAllStreaming() {
        InputStream example = IOUtil.createInput("src/data/tests/example.csv");
        IDataAccess csv = new CsvFactory().setOption("streaming").newInstance(example);
        Stream<IDataAccess> s = csv.getAll(null);
        IDataAccess[] rows = s.toArray(IDataAccess[]::new);
        
        assertEquals("1997", rows[0].get("Year").getContents());
        assertEquals("", rows[1].get("Description").getContents());
        assertEquals("Venture \"Extended Edition, Very Large\"", rows[3].get("Model").getContents());
    }
    
    @Test
    public void testSchema() {
        InputStream example = IOUtil.createInput("src/data/tests/example.csv");
        CsvDataAccess csv = new CsvFactory().setOption("streaming").newInstance(example);
        
        ISchema expected = new ListSchema(new CompSchema(
                new CompField("Year", new PrimSchema("Year")),
                new CompField("Make", new PrimSchema("Make")),
                new CompField("Model", new PrimSchema("Model")),
                new CompField("Description", new PrimSchema("Description")),
                new CompField("Price", new PrimSchema("Price"))));
        ISchema actual = csv.getSchema();
        assertEquals(expected.toString(true), actual.toString(true));
    }
    
    @Test
    public void testInfer() {
        CsvInfer ci1 = new CsvInfer();
        CsvInfer ci2 = new CsvInfer("\t");
        assertEquals(true, ci1.matchedBy("example.csv"));
        assertEquals(false, ci2.matchedBy("example.csv"));
    }
}

