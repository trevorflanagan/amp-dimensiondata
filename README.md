jclouds-dimensiondata-cloudcontroller is a jclouds api modelled on DimensionData CloudController API.


# How to use it

This provider assumes that you have configured your DimnesionData account. In particular, you need to have defined, at least:

- use a MCP2.0 datacenter
- a `NetworkDomain` with a `vlanId` where jclouds will attach VMs to

# Known Limitations

it supports only MCP2.0 datacenters
It currently supports only public images.
