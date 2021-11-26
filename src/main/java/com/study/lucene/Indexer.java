package com.study.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Indexer
{
    private IndexWriter writer;

    public Indexer(String indexDir) throws IOException
    {
        final Path path = Paths.get(indexDir);
        Directory dir = FSDirectory.open(path);

        //루씬의 IndexWriter 생성
        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
        writer = new IndexWriter(dir, indexWriterConfig);
    }

    public static void main(String[] args) throws Exception {

        System.out.println(args.length);
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage : java " + Indexer.class.getName() + "<index dir> <data dir>");
        }

        String indexDir = args[0];        // 지정 디렉터리에 색인 생성
        String dataDir = args[1];        // 해당 디렉터리에 담긴 txt파일을 색인할 예정이다.

        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed;

        try {
            numIndexed = indexer.index(dataDir);
        } finally {
            indexer.close();
        }

        long end = System.currentTimeMillis();

        System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds");
    }

    private void close() throws IOException {
        writer.close();

    }

    private int index(String dataDir) throws Exception {
        File[] files = new File(dataDir).listFiles();

        for (File f : files) {
            if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead()) {
                indexFile(f);
            }
        }
        return writer.numDocs();
    }

    private void indexFile(File f) throws Exception {
        System.out.println("Indexing "+f.getCanonicalPath());
        Document doc = getDocument(f);
        writer.addDocument(doc);

    }

    private Document getDocument(File f) throws Exception {
        Document doc = new Document();
        doc.add(new TextField("contents", new FileReader(f)));

        doc.add(new TextField("filename", f.getName(), Field.Store.YES));
        doc.add(new TextField("fullpath", f.getCanonicalPath(),Field.Store.YES));
        return doc;
    }
}
