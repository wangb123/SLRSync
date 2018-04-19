/**
 * Copyright 2013 Nils Assbeck, Guersel Ayaz and Michael Zoech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tumao.sync.ptp.commands;

import com.tumao.sync.ptp.PtpCamera;
import com.tumao.sync.ptp.PtpConstants;
import com.tumao.sync.ptp.model.DevicePropDesc;

import java.nio.ByteBuffer;

public class GetDevicePropDescCommand extends Command {

    private final int property;
    private DevicePropDesc devicePropDesc;

    public GetDevicePropDescCommand(PtpCamera camera, int property) {
        super(camera);
        this.property = property;
    }

    @Override
    public void exec(PtpCamera.IO io) {
        io.handleCommand(this);
        if (responseCode == PtpConstants.Response.DeviceBusy) {
            camera.onDeviceBusy(this, true);
        }
        if (devicePropDesc != null) {
            // this order is important
            camera.onPropertyDescChanged(property, devicePropDesc);
            camera.onPropertyChanged(property, devicePropDesc.currentValue);
        }
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, PtpConstants.Operation.GetDevicePropDesc, property);
    }

    @Override
    protected void decodeData(ByteBuffer b, int length) {
        devicePropDesc = new DevicePropDesc(b, length);
    }
}
