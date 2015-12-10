# PodcastProcessor
Process podcasts for use on CDRW

Tested on Xbuntu 14.04 LTS. From the directories defined in main(),
this class will walk the file system looking for non-converted files
and downsample them, split them, blank a disk in the CDRW tray and 
write to an ISO file in the /tmp directory.
