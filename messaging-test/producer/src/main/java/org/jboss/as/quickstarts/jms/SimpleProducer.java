/*
 * Copyright 2005-2014 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.jboss.as.quickstarts.jms;

import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * @author Clebert Suconic
 */

public class SimpleProducer extends AbstractJMS
{
   public static void main(String arg[])
   {
      SimpleProducer producer = new SimpleProducer();
      try
      {
         producer.connect(arg[0], arg[1]);
         producer.run(Integer.parseInt(arg[2]));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }


   public void run(int sleepTime) throws Exception
   {
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      MessageProducer producer = session.createProducer(queue);


      System.out.println("sleeping " + sleepTime);
      long i = 0;
      while (true)
      {
         producer.send(session.createTextMessage("Message " + (i++)));
         if (sleepTime > 0)
         {
            Thread.sleep(sleepTime);
         }
         if (i % 100 == 0)
         {
            System.out.println("Sent " + i + " messages on " + queue);
         }
      }
   }
}