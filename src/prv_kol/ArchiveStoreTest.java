package prv_kol;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

class NonExistingItemException extends Exception{
    public NonExistingItemException(int id) {
        super(String.format("Item with id %d doesn't exist", id));
    }
}

class Archive{
    private int id;
    private LocalDate dateArchived;

    public Archive(int id, LocalDate dateArchived) {
        this.id = id;
        this.dateArchived = dateArchived;
    }

    public Archive(int id) {
        this.id = id;
    }

    public LocalDate getDateArchived() {
        return dateArchived;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateArchived(LocalDate dateArchived) {
        this.dateArchived = dateArchived;
    }

    public boolean isLocked(){
        return false;
    }
}

class LockedArchive extends Archive{
    LocalDate dateToOpen;
    public LockedArchive(int id, LocalDate dateToOpen) {
        super(id);
        this.dateToOpen=dateToOpen;
    }

    public LocalDate getDateToOpen() {
        return dateToOpen;
    }

    @Override
    public boolean isLocked() {
        return true;
    }
}
class SpecialArchive extends Archive{
    int maxOpen;
    int currentOpens;
    public SpecialArchive(int id, int maxOpen) {
        super(id);
        this.maxOpen=maxOpen;
        this.currentOpens=0;
    }

    public int getMaxOpen() {
        return maxOpen;
    }
    public void open(){
        currentOpens++;
    }

    public int getCurrentOpens() {
        return currentOpens;
    }
}

class ArchiveStore{
    List<Archive> archives;
    List<String> logs;

    public ArchiveStore() {
        this.archives=new ArrayList<>();
        this.logs=new ArrayList<>();
    }
    public void archiveItem(Archive item, LocalDate date){
        archives.add(item);
        archives.get(archives.size()-1).setDateArchived(date);
        logs.add("Item "+ item.getId() + " archived at " + date);

    }

    public void openItem(int id, LocalDate date) throws NonExistingItemException {
       Archive archive=archives.stream().filter(i->i.getId()==id).findFirst().orElse(null);

       if(archive== null){
           throw new NonExistingItemException(id);
       }
       if(archive.isLocked()){
           LockedArchive lockedArchive= (LockedArchive) archive;
           if(date.isBefore(lockedArchive.dateToOpen)){
               logs.add("Item " + lockedArchive.getId() + " cannot be opened before "+ lockedArchive.getDateToOpen());
               return;
           }
       }
       else {
           SpecialArchive specialArchive=(SpecialArchive) archive;
           if(specialArchive.getCurrentOpens() >= specialArchive.maxOpen){
               logs.add("Item " + specialArchive.getId() + " cannot be opened more than "
                       + specialArchive.getMaxOpen()+ " times");
               return;
           }
           specialArchive.open();
       }
       logs.add("Item "+ archive.getId() +" opened at " +archive.getDateArchived());

    }
        public String getLog() {
            return logs.stream()
                    .collect(Collectors.joining("\n", "", "\n"));
        }
}

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while(scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch(NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}