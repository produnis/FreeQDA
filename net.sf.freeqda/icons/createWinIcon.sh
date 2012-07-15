#*******************************************************************************
# FreeQDA,  a software for professional qualitative research data 
# analysis, such as interviews, manuscripts, journal articles, memos
# and field notes.
#
# Copyright (C) 2011 Dirk Kitscha, Jörg große Schlarmann
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#*******************************************************************************
#!/bin/bash

convert -colors 256 icon_16.png /tmp/icon_16_l.gif
convert -colors 256 /tmp/icon_16_l.gif icon_16_l.png

convert -colors 256 icon_32.png /tmp/icon_32_l.gif
convert -colors 256 /tmp/icon_32_l.gif icon_32_l.png

convert -colors 256 icon_48.png /tmp/icon_48_l.gif
convert -colors 256 /tmp/icon_48_l.gif icon_48_l.png

icotool -c icon_16_l.png icon_16.png icon_32_l.png icon_32.png icon_48_l.png icon_48.png > icon.ico

