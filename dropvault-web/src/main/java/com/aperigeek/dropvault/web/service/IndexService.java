/*  
 * This file is part of dropvault.
 *
 * dropvault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dropvault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with dropvault.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aperigeek.dropvault.web.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Vivien Barousse
 */
@Stateless
public class IndexService {
    
    private static final File INDEX_FOLDER = new File("/home/dropvault/indexes");
    
    public void index(String username, String password, 
            String id, Map<String, String> metadata) throws IndexException {
        try {
            Document document = new Document();
            document.add(new Field("id", id, Field.Store.YES, Field.Index.NOT_ANALYZED));
            for (Map.Entry<String, String> e : metadata.entrySet()) {
                if (e.getValue() != null) {
                    document.add(new Field(e.getKey(), e.getValue(), 
                            Field.Store.NO, Field.Index.ANALYZED));
                }
            }
            
            IndexWriter index = getIndexWriter(username, password);
            index.addDocument(document);
            index.close();
        } catch (IOException ex) {
            throw new IndexException(ex);
        }
    }
    
    public void remove(String username, String password, String id) throws IndexException {
        try {
            IndexWriter writer = getIndexWriter(username, password);
            writer.deleteDocuments(new Term("id", id));
            writer.close();
        } catch (IOException ex) {
            throw new IndexException(ex);
        }
    }
    
    public List<String> search(String username, String password, String query) throws IndexException {
        try {
            IndexSearcher searcher = getIndexSearcher(username, password);
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_33);
            QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_33, new String[]{"title", "body"}, analyzer);
            Query luceneQuery = parser.parse(query);
            TopDocs docs = searcher.search(luceneQuery, 10);
            List<String> results = new ArrayList<String>();
            for (ScoreDoc doc : docs.scoreDocs) {
                results.add(searcher.doc(doc.doc).getFieldable("id").stringValue());
            }
            searcher.close();
            return results;
        } catch (IOException ex) {
            throw new IndexException(ex);
        } catch (ParseException ex) {
            throw new IndexException("Invalid query syntax", ex);
        }
    }
    
    private IndexSearcher getIndexSearcher(String username, String password) throws IOException {
        IndexSearcher searcher = new IndexSearcher(getDirectory(username, password));
        return searcher;
    }
    
    private IndexWriter getIndexWriter(String username, String password) throws IOException {
        Analyzer analyser = new StandardAnalyzer(Version.LUCENE_33);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_33, analyser);
        IndexWriter writer = new IndexWriter(getDirectory(username, password), config);
        return writer;
    }
    
    private Directory getDirectory(String username, String password) throws IOException {
        File userIndex = new File(INDEX_FOLDER, username);
        if (!userIndex.exists()) {
            userIndex.mkdirs();
        }
        Directory directory = new SimpleFSDirectory(userIndex);
        return directory;
    }
    
}
