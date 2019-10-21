package nsu.manasyan.treechat.util;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ArgResolver {

    private final static int NAME_INDEX = 0;

    private final static int PORT_INDEX = 1;

    private final static int LOSS_PERCENTAGE_INDEX = 2;

    private final static int NEIGHBOUR_ADDRESS_INDEX = 3;

    private final static int NEIGHBOUR_PORT_INDEX = 4;

    private final static int ARGS_COUNT = 3;

    private final static int EXTENDED_ARGS_COUNT = 5;

    public static Options resolve(String[] args) throws IOException {
        if(args.length != ARGS_COUNT && args.length != EXTENDED_ARGS_COUNT){
            throw new IOException("Wrong arguments count.\nUsage: " + getUsage());
        }
        Options options = new Options();

        try {
            options.setName(args[NAME_INDEX]);
            options.setPort(Integer.parseInt(args[PORT_INDEX]));
            options.setLossPercentage(Integer.parseInt(args[LOSS_PERCENTAGE_INDEX]));

            if(args.length == EXTENDED_ARGS_COUNT){
                options.setAlternate(new InetSocketAddress(args[NEIGHBOUR_ADDRESS_INDEX],
                        Integer.parseInt(args[NEIGHBOUR_PORT_INDEX])));
            }

        } catch (Exception e){
            throw new IOException(e.getLocalizedMessage() + "\n" + getUsage());
        }

        return options;
    }

    private static String getUsage(){
        return "treeChat <node_name> <port> <loss_percentage> [<neighbour_address> <neighbour_port>]";
    }
}
