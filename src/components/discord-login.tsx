'use client'

import { useState } from 'react'
import { Button } from "~/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "~/components/ui/card"
import { DiscordLogoIcon } from '@radix-ui/react-icons'
import { authOptions } from "~/server/auth";

export function DiscordLogin() {
  const [isLoading, setIsLoading] = useState(false)

  const handleLogin = () => {
    setIsLoading(true)

    // Here you would typically redirect to Discord OAuth
    setTimeout(() => setIsLoading(false), 2000) // Simulating API call
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-gray-900 to-gray-800">
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-purple-950 rounded-full blur-3xl opacity-15 animate-pulse-slow"></div>
      </div>
      <Card className="w-full max-w-md bg-gray-800 text-gray-100 shadow-2xl">
        <CardHeader className="space-y-1">
          <CardTitle className="text-2xl font-bold text-center">Login</CardTitle>
          <CardDescription className="text-center text-gray-400">
            Sign in to your account using Discord
          </CardDescription>
        </CardHeader>
        <CardContent className="flex justify-center">
          <Button
            className="w-full max-w-sm bg-indigo-600 hover:bg-indigo-700 text-white"
            onClick={handleLogin}
            disabled={isLoading}
          >
            {isLoading ? (
              <div className="flex items-center justify-center">
                <div className="w-5 h-5 border-t-2 border-b-2 border-white rounded-full animate-spin mr-2"></div>
                Connecting...
              </div>
            ) : (
              <div className="flex items-center justify-center">
                <DiscordLogoIcon className="w-5 h-5 mr-2" />
                Login with Discord
              </div>
            )}
          </Button>
        </CardContent>
        <CardFooter className="text-sm text-center text-gray-500">
          By signing in, you agree to our Terms of Service and Privacy Policy
        </CardFooter>
      </Card>
    </div>
  )
}