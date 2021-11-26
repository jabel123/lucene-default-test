package com.study.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.CollectorManager;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Searcher
{
    public static void main(String[] args) throws Exception{
        if(args.length != 2){
            throw new IllegalArgumentException("Usage : java "+Searcher.class.getName()+" <index dir> <query>");
        }

        String indexDir=args[0]; //색인 디렉토리
        String q=args[1]; // 검색 쿼리
        search(indexDir, q);

    }

    private static void search(String indexDir, String q) throws Exception {
        final Path path = Paths.get(indexDir);
        Directory dir= FSDirectory.open(path);

        IndexSearcher is=new IndexSearcher(DirectoryReader.open(dir));

        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query=parser.parse(q);

        long start=System.currentTimeMillis();

        //TopDocs hits=is.search(query, 5);
        final TopDocs hits = is.search(query, 10);

        long end=System.currentTimeMillis();


        System.err.println("Found "+hits.totalHits + " document(s) (in "+(end-start)+" milliseconds) that matched query'"+q+"':");

        for(ScoreDoc scoreDoc : hits.scoreDocs){
            Document doc=is.doc(scoreDoc.doc);
            System.out.println(doc.get("filename"));
        }
    }
}
