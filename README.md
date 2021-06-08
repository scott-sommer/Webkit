Webkit
======

This was a very quick architectural mock I did when I was working at Clipsal. The Smart Devices Department within Schneider Electric has many Smart Electronic devices globally, and in Clipsal's CBUS portfolio there were more than 420 unique devices at the time ranging from simple Smart light switches to Commercial Building Programmable Logic Controllers. This mock was part of a discussion my team had internally about how we could rapidly offer our customers an application which generated UIs to program these products using existing configuration files we already had for programming the units in the production factories.

While we didn't go with this implementation making use of Vaadin, we did go with a java/spring stack for the backend service, and a C#/UWP client application due to the team's existing skill set in those technologies, and our customer's prolific use of Windows OS machines including the Surface Pro.
