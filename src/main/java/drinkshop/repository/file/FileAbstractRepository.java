package drinkshop.repository.file;

import drinkshop.repository.AbstractRepository;

import java.io.*;

public abstract class FileAbstractRepository<ID, E>
        extends AbstractRepository<ID, E> {

    protected String fileName;

    protected FileAbstractRepository(String fileName) {
        this.fileName = fileName;
    }

    protected void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                E entity = extractEntity(line);
                super.save(entity);
            }

        } catch (IOException e) {
            // C08: Error message processing errors exist
            // Observatie: Inlocuim printStackTrace cu o exceptie care propaga eroarea critica
            throw new RuntimeException("Eroare critica la citirea fisierului: " + fileName, e);
        }
    }

    private void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {

            for (E entity : entities.values()) {
                bw.write(createEntityAsString(entity));
                bw.newLine();
            }

        } catch (IOException e) {
            // C08: Error message processing errors exist
            throw new RuntimeException("Eroare critica la scrierea in fisierul: " + fileName, e);
        }
    }

    @Override
    public E save(E entity) {
        E e = super.save(entity);
        writeToFile();
        return e;
    }

    @Override
    public E delete(ID id) {
        E e = super.delete(id);
        writeToFile();
        return e;
    }

    @Override
    public E update(E entity) {
        E e = super.update(entity);
        writeToFile();
        return e;
    }

    protected abstract E extractEntity(String line);

    protected abstract String createEntityAsString(E entity);
}
