package ch.skyfy.singlebackpack;

public class BackpacksManager {

    public static Size current = SingleBackpack.config.sizes.get(0L);

    public static void initialize(){
        // Determines when the player's doc bag expands
        PlayTimeMeter.getInstance().registerTimeChangedEvent(time -> {
            Size size = null;
            var it = SingleBackpack.config.sizes.entrySet().iterator();
            while (it.hasNext()){
                var entry = it.next();
                if(time >= entry.getKey()){
                    if(it.hasNext()){
                        var next = it.next();
                        if(time < next.getKey()) size = next.getValue();
                    }else size = entry.getValue();
                }
            }
            if(size != null && !size.equals(current)) current = size;
        });
    }
}
