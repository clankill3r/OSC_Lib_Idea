import static java.lang.System.out;

public class OSC_Lib_Idea  {

    public static void main(String[] args) {
        OSC_Lib_Idea s = new OSC_Lib_Idea();
        s.test();
    }


    void test() {

        OSC_Handler root =
        osc("/mobile_device", 

            msg((m)-> {
                out.println("some mobile_device message!");
            }),
           
            osc("/gyroscope"),
            osc("/compass", msg((m)-> {
                if (m.type_tag.equals("f")) {
                    float a = to_float(m.data[0]);
                    out.println(String.format("compass: %f", a));
                }
            })),
            osc("/accelerometer"),
            osc("/touches", msg((m)-> {
                    if (m.type_tag.equals("iii")) {
                        int id = to_int(m.data[0]);
                        int x  = to_int(m.data[1]);
                        int y  = to_int(m.data[2]);
                        out.println(String.format("id: %d, x: %d, y: %d", id, x, y));
                    }
                }),
                osc("/gestures")
            )
          
        );       

        OSC_Message msg = new OSC_Message();
        msg.addr_remainder = "/mobile_device/touches";
        msg.type_tag = "iii";
        msg.data = new Object[] {0, 234, 765};

        handle(msg, root);

        msg = new OSC_Message();
        msg.addr_remainder = "/mobile_device/compass";
        msg.type_tag = "f";
        msg.data = new Object[] {123.4f};

        handle(msg, root);

        msg = new OSC_Message();
        msg.addr_remainder = "/mobile_device/touches/gestures";
        msg.type_tag = "";
        msg.data = new Object[] {};

        handle(msg, root);



      

    }




    public OSC_Handler osc(String addr, OSC_Handler... osc_handlers) {

        OSC_Handler osc_handler = new OSC_Handler();
        osc_handler.addr = addr;
        osc_handler.handlers = osc_handlers;
        return osc_handler;
    }
  

    public OSC_Handler msg(OSC_Msg_Handler m) {

        OSC_Handler osc_handler = new OSC_Handler();
        osc_handler.msg_handler = m;
        return osc_handler;
    }


    interface OSC_Msg_Handler {
        void handle(OSC_Message m);
    }


    public class OSC_Handler {
        String addr = "";
        OSC_Handler handlers[] = new OSC_Handler[0];
        OSC_Msg_Handler msg_handler;
    }




    public void handle(OSC_Message msg, OSC_Handler handler) {
        
        if (msg.addr_remainder.startsWith(handler.addr)) {

            msg.addr_remainder = msg.addr_remainder.substring(handler.addr.length());
            
            if (handler.msg_handler != null) {
                handler.msg_handler.handle(msg);
            }

            for (OSC_Handler handler2 : handler.handlers) {
                handle(msg, handler2);
            }

        }
    }


    class OSC_Message {
        String addr_remainder;
        String type_tag;
        Object[] data;

        int as_int() {
            return -1;
        }
    }

    // dangerous to use...
    int to_int(Object o) {
        if (o instanceof Integer) {
            return (Integer) o;
        }
        return Integer.parseInt((String)o);
    }

    float to_float(Object o) {
        if (o instanceof Float) {
            return (Float) o;
        }
        return Float.parseFloat((String)o);
    }

}
