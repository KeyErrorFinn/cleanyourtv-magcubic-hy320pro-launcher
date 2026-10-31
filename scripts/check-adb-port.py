"""Bounded TCP check of one confirmed projector endpoint; timeout is inconclusive."""
import argparse
import errno
import socket

def classify(error):
    if isinstance(error, ConnectionRefusedError) or getattr(error,'errno',None) in (errno.ECONNREFUSED,10061):
        return 'refused'
    return 'inconclusive'

def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--host',required=True,help='Confirmed projector IP or hostname; no discovery/scanning')
    parser.add_argument('--port',type=int,required=True)
    args=parser.parse_args()
    if not 1<=args.port<=65535: parser.error('port must be 1 through 65535')
    try:
        with socket.create_connection((args.host,args.port),timeout=3):
            print('Reachable TCP service. This alone does not identify or authenticate ADB.')
            return 1
    except OSError as error:
        if classify(error)=='refused':
            print('Explicit connection refusal at this endpoint. No general firmware-security claim.')
            return 0
        print('Inconclusive: '+type(error).__name__+'. Timeout/routing/local permissions do not prove closure.')
        return 2

if __name__=='__main__': raise SystemExit(main())
