import usb.core
import usb.util
import sys
import os
import binascii

print (sys.path)
os.environ['PYUSB_DEBUG']='debug'
os.environ['PYUSB_LOG_FILENAME']='debug'

print ("HHHHHHHHEEEEERRRRRRRE")
dev = usb.core.find(idVendor=0x268b, idProduct=0x0201)
 
if dev is None:
    raise ValueError('Device not found')
print ("found saber device")

iface=0

c = 1
for config in dev:
    print ('config', c)
    print ('Interfaces', config.bNumInterfaces)
    for i in range(config.bNumInterfaces):
        try:
           print('detaching interface num ',i);
           dev.detach_kernel_driver(i);
        except:
           pass
    c+=1

reattach = False

print("setting config...");
dev.set_configuration()
print("...set config!!!!!");


print ("descriptorType: [" + str(dev.bDescriptorType) + "]");
print ("numConfigs    : [" + str(dev.bNumConfigurations) + "]");
conf = dev.__getitem__(0);
print("num infaces: [" + str(conf.bNumInterfaces) + "]");

#print (conf);
print ("configuration : [" + str(conf) + "]");

interface = conf[(2,0)];
print ("interface : [" + str(interface) + "]");


print("num endpoints: [" + str(interface.bNumEndpoints) + "]");
print("ep0 : [" + str(interface.__getitem__(0).bEndpointAddress) + "]");
print("ep1 : [" + str(interface.__getitem__(1).bEndpointAddress) + "]");
ep0 = interface.__getitem__(0);
ep1 = interface.__getitem__(1);
print('ep0 addr: ',ep0.bEndpointAddress);
print('ep1 addr: ',ep1.bEndpointAddress);


#print('writing...');
#ep1.write(b'\x6d\x31\x3a\x36\x36\x30\x0d\x0a');

print('writing m1:0...');
ep1.write(b'\x6d\x31\x3a\x30\x0d\x0a');
print('writing m2:0...');
ep1.write(b'\x6d\x32\x3a\x30\x0d\x0a');
print('...wrote');

#print('writing m1:get...')
#ep1.write(b'\x6d\x31\x3a\x67\x65\x74\x0d\x0a');

#print('reading...');
#data = dev.read(ep0.bEndpointAddress,7) #ep0.wMaxPacketSize)
#print ('Data is: ',data)
#d = ''
#for v in data:
#   d += str(v)

#print('or, ',d)


################################
#
#  END
#
###################################
usb.util.dispose_resources(dev)
if reattach:
   dev.attach_kernel_driver(0)

